package com.eresearch.repositorer.aggregator;

import com.eresearch.repositorer.domain.lookup.NameLookup;
import com.eresearch.repositorer.domain.record.Entry;
import com.eresearch.repositorer.domain.record.Record;
import com.eresearch.repositorer.exception.business.RepositorerBusinessException;
import com.eresearch.repositorer.exception.error.RepositorerError;
import com.eresearch.repositorer.repository.NamesLookupRepository;
import com.eresearch.repositorer.transformer.dto.TransformedEntriesDto;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j
@Component
public class TransformedResultsAggregator {

    private static final String TRANSACTION_ID = "Transaction-Id";

    @Autowired
    private NamesLookupRepository namesLookupRepository;

    @Autowired
    private Clock clock;

    public Message<?> aggregate(Collection<Message<?>> messages) {

        log.info("TransformedResultsAggregator --> " + Thread.currentThread().getName());

        final List<TransformedEntriesDto> transformedEntriesDtos = extractTransformedEntriesDtos(messages);

        ensureUniqueness(messages, transformedEntriesDtos);

        final String transactionId = extractTransactionId(messages);

        final NameLookup nameLookup = namesLookupRepository
                .findNamesLookupByTransactionIdEquals(transactionId);

        final Record record = produceResult(transformedEntriesDtos, nameLookup, transactionId);

        return MessageBuilder
                .withPayload(record)
                .setHeader(TRANSACTION_ID, transactionId)
                .build();
    }

    private List<TransformedEntriesDto> extractTransformedEntriesDtos(Collection<Message<?>> messages) {
        return messages
                .stream()
                .map(message -> (TransformedEntriesDto) message.getPayload())
                .collect(Collectors.toList());
    }

    private void ensureUniqueness(Collection<Message<?>> messages, List<TransformedEntriesDto> transformedEntriesDtos) {
        //check if we are talking for the same transaction id (uniqueness).
        final Set<String> transactionIds = extractTransactionIds(messages);
        if (transactionIds.size() != 1) {
            throw new RepositorerBusinessException(RepositorerError.APPLICATION_NOT_IN_CORRECT_STATE,
                    RepositorerError.APPLICATION_NOT_IN_CORRECT_STATE.getMessage(),
                    this.getClass().getName());
        }
    }

    private String extractTransactionId(Collection<Message<?>> messages) {
        return (String) messages
                .stream()
                .findFirst()
                .get()
                .getHeaders()
                .get(TRANSACTION_ID);
    }

    private Set<String> extractTransactionIds(Collection<Message<?>> messages) {
        return messages
                .stream()
                .map(Message::getHeaders)
                .map(messageHeaders -> (String) messageHeaders.get(TRANSACTION_ID))
                .collect(Collectors.toSet());
    }

    private Record produceResult(List<TransformedEntriesDto> transformedEntriesDtos, NameLookup nameLookup, String transactionId) {

        List<Entry> entries = transformedEntriesDtos
                .stream()
                .map(TransformedEntriesDto::getEntries)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return new Record(UUID.randomUUID().toString(),
                transactionId,
                nameLookup.getFirstname(),
                nameLookup.getInitials(),
                nameLookup.getSurname(),
                nameLookup.getNameVariants(),
                Instant.now(clock),
                entries);
    }

}
