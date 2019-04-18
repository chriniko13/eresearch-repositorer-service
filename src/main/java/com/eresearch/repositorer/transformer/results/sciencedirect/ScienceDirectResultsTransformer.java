package com.eresearch.repositorer.transformer.results.sciencedirect;

import com.eresearch.repositorer.domain.record.Author;
import com.eresearch.repositorer.domain.record.Entry;
import com.eresearch.repositorer.domain.record.metadata.MetadataLabelsHolder;
import com.eresearch.repositorer.domain.record.metadata.Source;
import com.eresearch.repositorer.dto.sciencedirect.response.*;
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
public class ScienceDirectResultsTransformer implements ResultsTransformer {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProcessMetadataPredicate processMetadataPredicate;

    @Override
    public Message<?> transform(Message<?> message) {

        log.info("ScienceDirectResultsTransformer --> " + Thread.currentThread().getName());

        try {

            final SciDirQueueResultDto sciDirQueueResultDto = deserializeMessage(message);

            final List<Entry> transformedEntries = doProcessing(sciDirQueueResultDto);

            final String transactionId = sciDirQueueResultDto.getTransactionId();

            return MessageBuilder
                    .withPayload(new TransformedEntriesDto(transformedEntries))
                    .setHeader(TRANSACTION_ID, transactionId)
                    .build();

        } catch (IOException error) {
            log.error("ScienceDirectResultsTransformer#transform --- error occurred, error: " + error.toString(), error);

            throw new RepositorerBusinessException(RepositorerError.COULD_NOT_DESERIALIZE_MESSAGE,
                    RepositorerError.COULD_NOT_DESERIALIZE_MESSAGE.getMessage(),
                    error,
                    this.getClass().getName());
        }
    }

    private SciDirQueueResultDto deserializeMessage(Message<?> message) throws IOException {
        final String resultAsString = (String) message.getPayload();
        return objectMapper.readValue(resultAsString, new TypeReference<SciDirQueueResultDto>() {
        });
    }

    private List<Entry> doProcessing(SciDirQueueResultDto sciDirQueueResultDto) throws JsonProcessingException {

        final List<Entry> transformedEntries = new ArrayList<>();

        ElsevierScienceDirectConsumerResultsDto elsevierScienceDirectConsumerResultsDto
                = sciDirQueueResultDto.getSciDirResultsDto();

        if (!elsevierScienceDirectConsumerResultsDto.getOperationResult()) {
            throw new RepositorerBusinessException(RepositorerError.COULD_NOT_PERFORM_OPERATION,
                    RepositorerError.COULD_NOT_PERFORM_OPERATION.getMessage(),
                    this.getClass().getName());
        }

        for (ScienceDirectConsumerResultsDto scienceDirectConsumerResultsDto : elsevierScienceDirectConsumerResultsDto.getResults()) {

            ScienceDirectConsumerSearchViewDto scienceDirectConsumerSearchViewDto = scienceDirectConsumerResultsDto.getScienceDirectConsumerSearchViewDto();

            if (noResultsToProcess(scienceDirectConsumerSearchViewDto)) continue;

            for (ScienceDirectSearchViewEntry sciDirEntry : scienceDirectConsumerSearchViewDto.getEntries()) {

                Entry entry = create(sciDirEntry);
                transformedEntries.add(entry);
            }

        }

        return transformedEntries;
    }

    private boolean noResultsToProcess(ScienceDirectConsumerSearchViewDto scienceDirectConsumerSearchViewDto) {
        return "0".equals(scienceDirectConsumerSearchViewDto.getTotalResults())
                && "0".equals(scienceDirectConsumerSearchViewDto.getStartIndex())
                && "0".equals(scienceDirectConsumerSearchViewDto.getItemsPerPage())
                && scienceDirectConsumerSearchViewDto.getEntries() != null
                && scienceDirectConsumerSearchViewDto.getEntries().size() == 1
                && scienceDirectConsumerSearchViewDto.getEntries().iterator().next().getError().equals("Result set was empty");
    }

    private Entry create(ScienceDirectSearchViewEntry sciDirEntry) throws JsonProcessingException {

        final Map<String, String> metadata;
        if (processMetadataPredicate.doMetadataProcessing()) {
            metadata = metadataPopulation(sciDirEntry);
        } else {
            metadata = null;
        }

        String dcTitle = sciDirEntry.getDcTitle();

        Set<Author> entryAuthors = new LinkedHashSet<>();
        if (sciDirEntry.getAuthors() != null) {

            Collection<ScienceDirectSearchViewEntryAuthor> authors = sciDirEntry.getAuthors().getAuthors();

            if (authors != null && !authors.isEmpty()) {

                entryAuthors = authors
                        .stream()
                        .map(author -> {

                            String name = author.getName();

                            String[] splittedName = name.split(" ");

                            Author authorRecord;

                            if (splittedName.length == 1) {
                                authorRecord = new Author(null, null, splittedName[0]);
                            } else if (splittedName.length == 2) {
                                authorRecord = new Author(splittedName[0], null, splittedName[1]);
                            } else { // > 2

                                List<String> temp = new ArrayList<>(splittedName.length - 1);
                                for (int i = 2; i < splittedName.length; i++) {
                                    temp.add(splittedName[i]);
                                }

                                String calculatedSurname = String.join(" ", temp);

                                authorRecord = new Author(splittedName[0], splittedName[1], calculatedSurname);

                            }

                            return authorRecord;

                        })
                        .collect(LinkedHashSet::new, Set::add, Set::addAll);
            }
        }

        return Entry
                .builder()
                .title(dcTitle)
                .authors(entryAuthors)
                .metadata(metadata)
                .build();
    }

    private Map<String, String> metadataPopulation(ScienceDirectSearchViewEntry sciDirEntry) throws JsonProcessingException {

        Map<String, String> metadata = new LinkedHashMap<>();
        metadata.put(MetadataLabelsHolder.SOURCE.getLabelName(),
                Source.SCIENCE_DIRECT.getSourceName());

        Collection<ScienceDirectSearchViewLink> links = sciDirEntry.getLinks();
        if (links != null && !links.isEmpty()) {
            List<String> linksAsHrefs = links
                    .stream()
                    .map(ScienceDirectSearchViewLink::getHref)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            String linksAsHrefsString = objectMapper.writeValueAsString(linksAsHrefs);
            metadata.put(MetadataLabelsHolder.SciDirLabels.LINKS.getLabelName(),
                    linksAsHrefsString);
        } else {
            metadata.put(MetadataLabelsHolder.SciDirLabels.LINKS.getLabelName(),
                    null);
        }


        String loadDate = sciDirEntry.getLoadDate();
        metadata.put(MetadataLabelsHolder.SciDirLabels.LOAD_DATE.getLabelName(), loadDate);

        String prismUrl = sciDirEntry.getPrismUrl();
        metadata.put(MetadataLabelsHolder.SciDirLabels.PRISM_URL.getLabelName(),
                prismUrl);

        String dcIdentifier = sciDirEntry.getDcIdentifier();
        metadata.put(MetadataLabelsHolder.SciDirLabels.DC_IDENTIFIER.getLabelName(),
                dcIdentifier);

        String openAccess = sciDirEntry.getOpenAccess();
        metadata.put(MetadataLabelsHolder.SciDirLabels.OPEN_ACCESS.getLabelName(),
                openAccess);

        String openAccessFlag = sciDirEntry.getOpenAccessFlag();
        metadata.put(MetadataLabelsHolder.SciDirLabels.OPEN_ACCESS_FLAG.getLabelName(),
                openAccessFlag);

        String dcTitle = sciDirEntry.getDcTitle();
        metadata.put(MetadataLabelsHolder.SciDirLabels.DC_TITLE.getLabelName(),
                dcTitle);

        String prismPublicationName = sciDirEntry.getPrismPublicationName();
        metadata.put(MetadataLabelsHolder.SciDirLabels.PRISM_PUBLICATION_NAME.getLabelName(),
                prismPublicationName);

        String prismIsbn = sciDirEntry.getPrismIsbn();
        metadata.put(MetadataLabelsHolder.SciDirLabels.PRISM_ISBN.getLabelName(),
                prismIsbn);

        String prismIssn = sciDirEntry.getPrismIssn();
        metadata.put(MetadataLabelsHolder.SciDirLabels.PRISM_ISSN.getLabelName(),
                prismIssn);

        String prismVolume = sciDirEntry.getPrismVolume();
        metadata.put(MetadataLabelsHolder.SciDirLabels.PRISM_VOLUME.getLabelName(),
                prismVolume);

        String prismIssueIdentifier = sciDirEntry.getPrismIssueIdentifier();
        metadata.put(MetadataLabelsHolder.SciDirLabels.PRISM_ISSUE_IDENTIFIER.getLabelName(),
                prismIssueIdentifier);

        String prismIssueName = sciDirEntry.getPrismIssueName();
        metadata.put(MetadataLabelsHolder.SciDirLabels.PRISM_ISSUE_NAME.getLabelName(),
                prismIssueName);

        String prismEdition = sciDirEntry.getPrismEdition();
        metadata.put(MetadataLabelsHolder.SciDirLabels.PRISM_EDITION.getLabelName(),
                prismEdition);

        String prismStartingPage = sciDirEntry.getPrismStartingPage();
        metadata.put(MetadataLabelsHolder.SciDirLabels.PRISM_STARTING_PAGE.getLabelName(),
                prismStartingPage);

        String prismEndingPage = sciDirEntry.getPrismEndingPage();
        metadata.put(MetadataLabelsHolder.SciDirLabels.PRISM_ENDING_PAGE.getLabelName(),
                prismEndingPage);

        String prismCoverDate = sciDirEntry.getPrismCoverDate();
        metadata.put(MetadataLabelsHolder.SciDirLabels.PRISM_COVER_DATE.getLabelName(), prismCoverDate);

        String coverDisplayDate = sciDirEntry.getCoverDisplayDate();
        metadata.put(MetadataLabelsHolder.SciDirLabels.PRISM_COVER_DISPLAY_DATE.getLabelName(),
                coverDisplayDate);

        String dcCreator = sciDirEntry.getDcCreator();
        metadata.put(MetadataLabelsHolder.SciDirLabels.DC_CREATOR.getLabelName(),
                dcCreator);

        ScienceDirectSearchViewEntryAuthors authors = sciDirEntry.getAuthors();
        if (authors != null
                && authors.getAuthors() != null
                && !authors.getAuthors().isEmpty()) {

            Collection<ScienceDirectSearchViewEntryAuthor> collectedAuthors = authors.getAuthors();

            String collectedAuthorsAsString = objectMapper.writeValueAsString(collectedAuthors);

            metadata.put(MetadataLabelsHolder.SciDirLabels.AUTHORS.getLabelName(),
                    collectedAuthorsAsString);
        }

        String prismDoi = sciDirEntry.getPrismDoi();
        metadata.put(MetadataLabelsHolder.SciDirLabels.PRISM_DOI.getLabelName(),
                prismDoi);

        String pii = sciDirEntry.getPii();
        metadata.put(MetadataLabelsHolder.SciDirLabels.PII.getLabelName(),
                pii);

        String pubType = sciDirEntry.getPubType();
        metadata.put(MetadataLabelsHolder.SciDirLabels.PUBTYPE.getLabelName(),
                pubType);

        String prismTeaser = sciDirEntry.getPrismTeaser();
        metadata.put(MetadataLabelsHolder.SciDirLabels.PRISM_TEASER.getLabelName(),
                prismTeaser);

        String dcDescription = sciDirEntry.getDcDescription();
        metadata.put(MetadataLabelsHolder.SciDirLabels.DC_DESCRIPTION.getLabelName(),
                dcDescription);

        String authKeywords = sciDirEntry.getAuthKeywords();
        metadata.put(MetadataLabelsHolder.SciDirLabels.AUTHOR_KEYWORDS.getLabelName(),
                authKeywords);

        String prismAggregationType = sciDirEntry.getPrismAggregationType();
        metadata.put(MetadataLabelsHolder.SciDirLabels.PRISM_AGGREGATION_TYPE.getLabelName(),
                prismAggregationType);

        String prismCopyright = sciDirEntry.getPrismCopyright();
        metadata.put(MetadataLabelsHolder.SciDirLabels.PRISM_COPYRIGHT.getLabelName(),
                prismCopyright);

        String scopusId = sciDirEntry.getScopusId();
        metadata.put(MetadataLabelsHolder.SciDirLabels.SCOPUS_ID.getLabelName(),
                scopusId);

        String eid = sciDirEntry.getEid();
        metadata.put(MetadataLabelsHolder.SciDirLabels.EID.getLabelName(),
                eid);

        String scopusEid = sciDirEntry.getScopusEid();
        metadata.put(MetadataLabelsHolder.SciDirLabels.SCOPUS_EID.getLabelName(),
                scopusEid);

        String pubmedId = sciDirEntry.getPubmedId();
        metadata.put(MetadataLabelsHolder.SciDirLabels.PUBMED_ID.getLabelName(),
                pubmedId);

        String openAccessArticle = sciDirEntry.getOpenAccessArticle();
        metadata.put(MetadataLabelsHolder.SciDirLabels.OPEN_ACCESS_ARTICLE.getLabelName(),
                openAccessArticle);

        String openArchiveArticle = sciDirEntry.getOpenArchiveArticle();
        metadata.put(MetadataLabelsHolder.SciDirLabels.OPEN_ARCHIVE_ARTICLE.getLabelName(),
                openArchiveArticle);

        String openAccessUserLicense = sciDirEntry.getOpenAccessUserLicense();
        metadata.put(MetadataLabelsHolder.SciDirLabels.OPEN_ACCESS_USER_LICENSE.getLabelName(),
                openAccessUserLicense);

        if (sciDirEntry.getAuthors() != null) {

            Collection<ScienceDirectSearchViewEntryCollaboration> collaborations = sciDirEntry
                    .getAuthors()
                    .getCollaborations();

            if (collaborations != null && !collaborations.isEmpty()) {

                String collaborationsAsString = objectMapper.writeValueAsString(collaborations);
                metadata.put(MetadataLabelsHolder.SciDirLabels.AUTHORS_COLLABORATION.getLabelName(), collaborationsAsString);
            }
        }
        return metadata;
    }
}
