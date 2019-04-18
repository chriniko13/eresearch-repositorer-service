package com.eresearch.repositorer.transformer;

import com.eresearch.repositorer.connector.AuthorMatcherConnector;
import com.eresearch.repositorer.domain.record.Author;
import com.eresearch.repositorer.domain.record.Entry;
import com.eresearch.repositorer.domain.record.Record;
import com.eresearch.repositorer.dto.authormatcher.request.AuthorComparisonDto;
import com.eresearch.repositorer.dto.authormatcher.request.AuthorNameDto;
import com.eresearch.repositorer.dto.authormatcher.response.AuthorMatcherResultsDto;
import com.eresearch.repositorer.dto.authormatcher.response.StringMetricAlgorithm;
import com.eresearch.repositorer.exception.business.RepositorerBusinessException;
import com.eresearch.repositorer.transformer.dto.NameDto;
import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Log4j
@Component
public class AuthorEntriesMatchingTransformer {

    private static final String TRANSACTION_ID = "Transaction-Id";

    @Value("${author.entries.matching.string.metric.algorithm}")
    private String stringMetricAlgorithmToUse;

    private StringMetricAlgorithm stringMetricAlgorithmToUseEnum;

    @Value("${author.entries.matching.success.threshold}")
    private String successThreshold;

    private double successThresholdDouble;

    @Value("${apply.record.entries.filtering.with.author.matching}")
    private String applyRecordEntriesFilteringWithAuthorMatching;

    @Value("${author.entries.matching.multithread.approach}")
    private String isMultithreadApproach;

    @Autowired
    @Qualifier("authorEntriesMatchingExecutor")
    private ExecutorService authorEntriesMatchingExecutor;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthorMatcherConnector authorMatcherConnector;

    @Value("${author.entries.matching.size.splitting.threshold}")
    private String authorEntriesMatchingSizeSplitting;

    private Integer authorEntriesMatchingSizeSplittingInteger;

    @Value("${author.entries.matching.splitting.list.size}")
    private String authorEntriesMatchingSplittingListSize;

    private Integer authorEntriesMatchingSplittingListSizeInteger;

    @PostConstruct
    public void init() {
        successThresholdDouble = Double.valueOf(successThreshold);

        stringMetricAlgorithmToUseEnum = StringMetricAlgorithm.valueOf(stringMetricAlgorithmToUse);

        authorEntriesMatchingSizeSplittingInteger = Integer.valueOf(authorEntriesMatchingSizeSplitting);

        authorEntriesMatchingSplittingListSizeInteger = Integer.valueOf(authorEntriesMatchingSplittingListSize);
    }

    public Message<?> transform(Message<?> message) {
        log.info("AuthorEntriesMatchingTransformer --> " + Thread.currentThread().getName());

        Record record = (Record) message.getPayload();
        String transactionId = (String) message.getHeaders().get(TRANSACTION_ID);

        if (Boolean.valueOf(applyRecordEntriesFilteringWithAuthorMatching)) {
            doAuthorMatching(record);
        }

        return MessageBuilder
                .withPayload(record)
                .setHeader(TRANSACTION_ID, transactionId)
                .build();
    }

    private void doAuthorMatching(Record record) {
        final List<NameDto> nameDtosToCompareAgainst = getNameDtos(record);

        if (record.getEntries() == null || record.getEntries().isEmpty()) {
            return;
        }

        Collection<Entry> entries = record.getEntries();

        final Collection<Entry> filteredEntries = getEntriesListToPopulate();

        if (Boolean.valueOf(isMultithreadApproach) && entries.size() >= authorEntriesMatchingSizeSplittingInteger) {

            List<List<Entry>> splittedEntries = Lists.partition(new ArrayList<>(entries), authorEntriesMatchingSplittingListSizeInteger);

            List<CompletableFuture<Void>> workers = splittedEntries
                    .stream()
                    .map(splittedEntry -> CompletableFuture.runAsync(
                            () -> singleThreadApproach(splittedEntry, nameDtosToCompareAgainst, filteredEntries),
                            authorEntriesMatchingExecutor
                            )
                    )
                    .collect(Collectors.toList());

            workers.forEach(CompletableFuture::join);

        } else {

            singleThreadApproach(entries, nameDtosToCompareAgainst, filteredEntries);

        }

        record.setEntries(filteredEntries);
    }

    private Collection<Entry> getEntriesListToPopulate() {
        return Boolean.valueOf(isMultithreadApproach) ? Collections.synchronizedList(new LinkedList<>()) : new LinkedList<>();
    }

    private void singleThreadApproach(Collection<Entry> entries, List<NameDto> nameDtosToCompareAgainst, Collection<Entry> filteredEntries) {
        boolean keepEntry;
        for (Entry entry : entries) {

            Set<Author> entryAuthors = entry.getAuthors();
            if (entryAuthors == null || entryAuthors.isEmpty()) {
                continue;
            }

            keepEntry = false;

            for (NameDto nameDto : nameDtosToCompareAgainst) {
                for (Author author : entryAuthors) {
                    keepEntry = compare(keepEntry, nameDto, author);
                }
            }

            if (keepEntry) {
                filteredEntries.add(entry);
            }
        }
    }

    private List<NameDto> getNameDtos(Record record) {
        final List<NameDto> nameDtosToCompareAgainst = new LinkedList<>();

        nameDtosToCompareAgainst.add(new NameDto(record.getFirstname(), record.getInitials(), record.getLastname()));

        if (record.getNameVariants() != null && !record.getNameVariants().isEmpty()) {
            nameDtosToCompareAgainst.addAll(record
                    .getNameVariants()
                    .stream()
                    .map(nameVariant -> modelMapper.map(nameVariant, NameDto.class))
                    .collect(Collectors.toCollection(LinkedList::new)));
        }

        return nameDtosToCompareAgainst;
    }

    private boolean compare(boolean keepEntry, NameDto nameDto, Author author) {

        AuthorComparisonDto authorComparisonDto = new AuthorComparisonDto(
                modelMapper.map(nameDto, AuthorNameDto.class),
                modelMapper.map(author, AuthorNameDto.class));

        try {
            AuthorMatcherResultsDto authorMatcherResultsDto
                    = authorMatcherConnector.performAuthorMatchingWithRetries(authorComparisonDto);

            Double comparisonResultFloor = authorMatcherResultsDto
                    .getResults()
                    .get(stringMetricAlgorithmToUseEnum)
                    .getComparisonResultFloor();

            if (comparisonResultFloor >= successThresholdDouble) {
                keepEntry = true;
            }

        } catch (RepositorerBusinessException e) {
            log.error("AuthorEntriesMatchingTransformer#transform --- error occurred.", e);
            keepEntry = true; //Note: if also retries failed and we are here, we keep it and move on.
        }

        return keepEntry;
    }
}
