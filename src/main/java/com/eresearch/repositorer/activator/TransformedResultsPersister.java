package com.eresearch.repositorer.activator;

import com.eresearch.repositorer.domain.lookup.NameLookup;
import com.eresearch.repositorer.domain.lookup.NameLookupStatus;
import com.eresearch.repositorer.domain.record.Record;
import com.eresearch.repositorer.event.BatchExtractorWakeUpEvent;
import com.eresearch.repositorer.repository.DynamicExternalSystemMessagesAwaitingRepository;
import com.eresearch.repositorer.repository.NamesLookupRepository;
import com.eresearch.repositorer.repository.RecordRepository;
import com.google.common.eventbus.EventBus;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;


@Log4j
@Component
public class TransformedResultsPersister {

    private static final String TRANSACTION_ID = "Transaction-Id";

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private DynamicExternalSystemMessagesAwaitingRepository messagesAwaitingRepository;

    @Autowired
    private NamesLookupRepository namesLookupRepository;

    @Autowired
    private EventBus batchExtractorEventBus;

    public void persist(Message<?> message) {

        log.info("TransformedResultsPersister --> " + Thread.currentThread().getName());

        log.info("TransformedResultsPersister#persist --- message = " + message);

        final Record record = (Record) message.getPayload();
        final String transactionId = (String) message.getHeaders().get(TRANSACTION_ID);

        // -------- db specific operations --------
        recordRepository.store(record);

        messagesAwaitingRepository.deleteByTransactionIdEquals(transactionId);

        NameLookup fetchedNameLookupToUpdate = namesLookupRepository.findNamesLookupByTransactionIdEquals(transactionId);
        fetchedNameLookupToUpdate.setNameLookupStatus(NameLookupStatus.COMPLETED);
        namesLookupRepository.save(fetchedNameLookupToUpdate);


        // -------- wake up batch extractor --------
        batchExtractorEventBus.post(new BatchExtractorWakeUpEvent(true));
    }

}
