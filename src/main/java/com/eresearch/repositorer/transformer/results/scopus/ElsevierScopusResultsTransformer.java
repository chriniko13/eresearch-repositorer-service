package com.eresearch.repositorer.transformer.results.scopus;

import com.eresearch.repositorer.domain.record.Author;
import com.eresearch.repositorer.domain.record.Entry;
import com.eresearch.repositorer.domain.record.metadata.MetadataLabelsHolder;
import com.eresearch.repositorer.domain.record.metadata.Source;
import com.eresearch.repositorer.dto.scopus.response.*;
import com.eresearch.repositorer.exception.business.RepositorerBusinessException;
import com.eresearch.repositorer.exception.error.RepositorerError;
import com.eresearch.repositorer.transformer.dto.TransformedEntriesDto;
import com.eresearch.repositorer.transformer.results.ResultsTransformer;
import com.eresearch.repositorer.transformer.results.metadata.ProcessMetadataPredicate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Log4j
@Component
public class ElsevierScopusResultsTransformer implements ResultsTransformer {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProcessMetadataPredicate processMetadataPredicate;

    @Override
    public Message<?> transform(Message<?> message) {

        log.info("ElsevierScopusResultsTransformer --> " + Thread.currentThread().getName());

        final String transactionId;
        try {

            final ScopusFinderQueueResultDto scopusFinderQueueResultDto = deserializeMessage(message);

            final List<Entry> transformedEntries = doProcessing(scopusFinderQueueResultDto);

            transactionId = scopusFinderQueueResultDto.getTransactionId();

            return MessageBuilder
                    .withPayload(new TransformedEntriesDto(transformedEntries))
                    .setHeader(TRANSACTION_ID, transactionId)
                    .build();

        } catch (IOException error) {
            log.error("ElsevierScopusResultsTransformer#transform --- error occurred, error message: " + error.toString(), error);

            throw new RepositorerBusinessException(RepositorerError.COULD_NOT_DESERIALIZE_MESSAGE,
                    RepositorerError.COULD_NOT_DESERIALIZE_MESSAGE.getMessage(),
                    error,
                    this.getClass().getName());
        }
    }

    private ScopusFinderQueueResultDto deserializeMessage(Message<?> message) throws IOException {

        final String resultAsString = (String) message.getPayload();
        return objectMapper.readValue(resultAsString, new TypeReference<ScopusFinderQueueResultDto>() {
        });

    }

    private List<Entry> doProcessing(ScopusFinderQueueResultDto scopusFinderQueueResultDto) throws JsonProcessingException {

        final List<Entry> transformedEntries = new ArrayList<>();

        ElsevierScopusConsumerResultsDto elsevierScopusConsumerResultsDto = scopusFinderQueueResultDto.getScopusConsumerResultsDto();

        if (!elsevierScopusConsumerResultsDto.getOperationResult()) {
            throw new RepositorerBusinessException(RepositorerError.COULD_NOT_PERFORM_OPERATION,
                    RepositorerError.COULD_NOT_PERFORM_OPERATION.getMessage(),
                    this.getClass().getName());
        }

        for (ScopusConsumerResultsDto scopusConsumerResultsDto : elsevierScopusConsumerResultsDto.getResults()) {

            ScopusConsumerSearchViewDto scopusConsumerSearchViewDto = scopusConsumerResultsDto.getScopusConsumerSearchViewDto();

            if (noResultsToProcess(scopusConsumerSearchViewDto)) continue;

            for (ScopusSearchViewEntry entry : scopusConsumerSearchViewDto.getEntries()) {

                Entry entryToSave = createEntry(entry);
                transformedEntries.add(entryToSave);
            }
        }

        return transformedEntries;
    }

    private boolean noResultsToProcess(ScopusConsumerSearchViewDto scopusConsumerSearchViewDto) {
        return "0".equals(scopusConsumerSearchViewDto.getTotalResults())
                && "0".equals(scopusConsumerSearchViewDto.getStartIndex())
                && "0".equals(scopusConsumerSearchViewDto.getItemsPerPage())
                && scopusConsumerSearchViewDto.getEntries() != null
                && scopusConsumerSearchViewDto.getEntries().size() == 1
                && scopusConsumerSearchViewDto.getEntries().iterator().next().getError().equals("Result set was empty");
    }

    private Entry createEntry(ScopusSearchViewEntry entry) throws JsonProcessingException {

        final Map<String, String> metadata;
        if (processMetadataPredicate.doMetadataProcessing()) {
            metadata = metadataPopulation(entry);
        } else {
            metadata = null;
        }

        String dcTitle = entry.getDcTitle();

        Set<Author> entryAuthors = new LinkedHashSet<>();
        Collection<ScopusSearchAuthor> authors = entry.getAuthors();
        if (authors != null && !authors.isEmpty()) {

            entryAuthors = authors
                    .stream()
                    .map(author -> new Author(author.getGivenName(), author.getInitials(), author.getSurname()))
                    .collect(LinkedHashSet::new, Set::add, Set::addAll);
        }

        return Entry
                .builder()
                .title(dcTitle)
                .authors(entryAuthors)
                .metadata(metadata)
                .build();
    }

    private Map<String, String> metadataPopulation(ScopusSearchViewEntry entry) throws JsonProcessingException {

        Map<String, String> metadata = new LinkedHashMap<>();
        metadata.put(MetadataLabelsHolder.SOURCE.getLabelName(),
                Source.ELSEVIER_SCOPUS.getSourceName());

        Collection<ScopusSearchViewLink> links = entry.getLinks();
        if (links != null && !links.isEmpty()) {
            List<String> linksAsHrefs = links
                    .stream()
                    .map(ScopusSearchViewLink::getHref)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            String linksAsHrefsString = objectMapper.writeValueAsString(linksAsHrefs);
            metadata.put(MetadataLabelsHolder.ScopusLabels.LINKS.getLabelName(),
                    linksAsHrefsString);
        } else {
            metadata.put(MetadataLabelsHolder.ScopusLabels.LINKS.getLabelName(),
                    null);
        }

        String prismUrl = entry.getPrismUrl();
        metadata.put(MetadataLabelsHolder.ScopusLabels.PRISM_URL.getLabelName(),
                prismUrl);

        String dcIdentifier = entry.getDcIdentifier();
        metadata.put(MetadataLabelsHolder.ScopusLabels.DC_IDENTIFIER.getLabelName(),
                dcIdentifier);

        String eid = entry.getEid();
        metadata.put(MetadataLabelsHolder.ScopusLabels.EID.getLabelName(),
                eid);

        String dcTitle = entry.getDcTitle();
        metadata.put(MetadataLabelsHolder.ScopusLabels.DC_TITLE.getLabelName(),
                dcTitle);

        String prismAggregationType = entry.getPrismAggregationType();
        metadata.put(MetadataLabelsHolder.ScopusLabels.PRISM_AGGREGATION_TYPE.getLabelName(),
                prismAggregationType);

        String citedByCount = entry.getCitedByCount();
        metadata.put(MetadataLabelsHolder.ScopusLabels.CITED_BY_COUNT.getLabelName(),
                citedByCount);

        String prismPublicationName = entry.getPrismPublicationName();
        metadata.put(MetadataLabelsHolder.ScopusLabels.PRISM_PUBLICATION_NAME.getLabelName(),
                prismPublicationName);

        List<ScopusSearchPrismIsbn> prismIsbns = entry.getPrismIsbns();
        if (prismIsbns != null && !prismIsbns.isEmpty()) {

            String prismIsbnsAsString = objectMapper.writeValueAsString(prismIsbns);
            metadata.put(MetadataLabelsHolder.ScopusLabels.PRISM_ISBNS.getLabelName(), prismIsbnsAsString);

        } else {
            metadata.put(MetadataLabelsHolder.ScopusLabels.PRISM_ISBNS.getLabelName(), null);
        }


        String prismIssn = entry.getPrismIssn();
        metadata.put(MetadataLabelsHolder.ScopusLabels.PRISM_ISSN.getLabelName(),
                prismIssn);

        String prismEissn = entry.getPrismEissn();
        metadata.put(MetadataLabelsHolder.ScopusLabels.PRISM_EISSN.getLabelName(),
                prismEissn);

        String prismVolume = entry.getPrismVolume();
        metadata.put(MetadataLabelsHolder.ScopusLabels.PRISM_VOLUME.getLabelName(),
                prismVolume);

        String prismIssueIdentifier = entry.getPrismIssueIdentifier();
        metadata.put(MetadataLabelsHolder.ScopusLabels.PRISM_ISSUE_IDENTIFIER.getLabelName(),
                prismIssueIdentifier);

        String prismPageRange = entry.getPrismPageRange();
        metadata.put(MetadataLabelsHolder.ScopusLabels.PRISM_PAGE_RANGE.getLabelName(),
                prismPageRange);

        String prismCoverDate = entry.getPrismCoverDate();
        metadata.put(MetadataLabelsHolder.ScopusLabels.PRISM_COVER_DATE.getLabelName(),
                prismCoverDate);

        String prismCoverDisplayDate = entry.getPrismCoverDisplayDate();
        metadata.put(MetadataLabelsHolder.ScopusLabels.PRISM_COVER_DISPLAY_DATE.getLabelName(),
                prismCoverDisplayDate);

        String prismDoi = entry.getPrismDoi();
        metadata.put(MetadataLabelsHolder.ScopusLabels.PRISM_DOI.getLabelName(),
                prismDoi);

        String pii = entry.getPii();
        metadata.put(MetadataLabelsHolder.ScopusLabels.PII.getLabelName(),
                pii);

        String pubmedId = entry.getPubmedId();
        metadata.put(MetadataLabelsHolder.ScopusLabels.PUBMED_ID.getLabelName(),
                pubmedId);

        String orcId = entry.getOrcId();
        metadata.put(MetadataLabelsHolder.ScopusLabels.ORC_ID.getLabelName(),
                orcId);

        String dcCreator = entry.getDcCreator();
        metadata.put(MetadataLabelsHolder.ScopusLabels.DC_CREATOR.getLabelName(),
                dcCreator);

        Collection<ScopusSearchAffiliation> affiliations = entry.getAffiliations();
        if (affiliations != null && !affiliations.isEmpty()) {
            String affiliationsAsString = objectMapper.writeValueAsString(affiliations);
            metadata.put(MetadataLabelsHolder.ScopusLabels.AFFILIATIONS.getLabelName(),
                    affiliationsAsString);
        } else {
            metadata.put(MetadataLabelsHolder.ScopusLabels.AFFILIATIONS.getLabelName(),
                    null);
        }

        Collection<ScopusSearchAuthor> authors = entry.getAuthors();
        if (authors != null && !authors.isEmpty()) {
            String authorsAsString = objectMapper.writeValueAsString(authors);
            metadata.put(MetadataLabelsHolder.ScopusLabels.AUTHORS.getLabelName(),
                    authorsAsString);
        } else {
            metadata.put(MetadataLabelsHolder.ScopusLabels.AUTHORS.getLabelName(),
                    null);
        }

        ScopusSearchAuthorCount authorCount = entry.getAuthorCount();
        if (authorCount != null && authorCount.getAuthorCount() != null) {
            metadata.put(MetadataLabelsHolder.ScopusLabels.AUTHOR_COUNT.getLabelName(),
                    authorCount.getAuthorCount());
        } else {
            metadata.put(MetadataLabelsHolder.ScopusLabels.AUTHOR_COUNT.getLabelName(),
                    null);
        }

        String dcDescription = entry.getDcDescription();
        metadata.put(MetadataLabelsHolder.ScopusLabels.DC_DESCRIPTION.getLabelName(),
                dcDescription);

        String authorKeywords = entry.getAuthorKeywords();
        metadata.put(MetadataLabelsHolder.ScopusLabels.AUTHOR_KEYWORDS.getLabelName(),
                authorKeywords);

        String articleNumber = entry.getArticleNumber();
        metadata.put(MetadataLabelsHolder.ScopusLabels.ARTICLE_NUMBER.getLabelName(),
                articleNumber);

        String subtype = entry.getSubtype();
        metadata.put(MetadataLabelsHolder.ScopusLabels.SUBTYPE.getLabelName(),
                subtype);

        String subtypeDescription = entry.getSubtypeDescription();
        metadata.put(MetadataLabelsHolder.ScopusLabels.SUBTYPE_DESCRIPTION.getLabelName(),
                subtypeDescription);

        String sourceId = entry.getSourceId();
        metadata.put(MetadataLabelsHolder.ScopusLabels.SOURCE_ID.getLabelName(),
                sourceId);

        String fundingAgencyAcronym = entry.getFundingAgencyAcronym();
        metadata.put(MetadataLabelsHolder.ScopusLabels.FUNDING_AGENCY_ACRONYM.getLabelName(),
                fundingAgencyAcronym);

        String fundingAgencyIdentification = entry.getFundingAgencyIdentification();
        metadata.put(MetadataLabelsHolder.ScopusLabels.FUNDING_AGENCY_IDENTIFICATION.getLabelName(),
                fundingAgencyIdentification);

        String fundingAgencyName = entry.getFundingAgencyName();
        metadata.put(MetadataLabelsHolder.ScopusLabels.FUNDING_AGENCY_NAME.getLabelName(),
                fundingAgencyName);

        String message = entry.getMessage();
        metadata.put(MetadataLabelsHolder.ScopusLabels.MESSAGE.getLabelName(),
                message);

        String openAccess = entry.getOpenAccess();
        metadata.put(MetadataLabelsHolder.ScopusLabels.OPEN_ACCESS.getLabelName(), openAccess);

        String openAccessFlag = entry.getOpenAccessFlag();
        metadata.put(MetadataLabelsHolder.ScopusLabels.OPEN_ACCESS_FLAG.getLabelName(), openAccessFlag);

        return metadata;
    }
}
