package com.eresearch.repositorer.activator

import com.eresearch.repositorer.domain.lookup.NameLookup
import com.eresearch.repositorer.domain.record.Record
import com.eresearch.repositorer.event.BatchExtractorWakeUpEvent
import com.eresearch.repositorer.repository.DynamicExternalSystemMessagesAwaitingRepository
import com.eresearch.repositorer.repository.NamesLookupRepository
import com.eresearch.repositorer.repository.RecordRepository
import com.google.common.eventbus.EventBus
import org.springframework.integration.support.MessageBuilder
import org.springframework.messaging.Message
import spock.lang.Specification

class TransformedResultsPersisterSpec extends Specification {

    TransformedResultsPersister transformedResultsPersister

    RecordRepository mockedRecordRepository
    DynamicExternalSystemMessagesAwaitingRepository mockedDynamicExternalSystemMessagesAwaitingRepository
    NamesLookupRepository mockedNamesLookupRepository
    EventBus mockedEventBus


    def setup() {

        mockedRecordRepository = Mock(RecordRepository)
        mockedDynamicExternalSystemMessagesAwaitingRepository = Mock(DynamicExternalSystemMessagesAwaitingRepository)
        mockedNamesLookupRepository = Mock(NamesLookupRepository)
        mockedEventBus = Mock(EventBus)

        transformedResultsPersister = new TransformedResultsPersister(
                recordRepository: mockedRecordRepository,
                messagesAwaitingRepository: mockedDynamicExternalSystemMessagesAwaitingRepository,
                namesLookupRepository: mockedNamesLookupRepository,
                batchExtractorEventBus: mockedEventBus
        )
    }

    def "persist method works as expected"() {

        given:
            NameLookup nameLookup = new NameLookup()
            BatchExtractorWakeUpEvent event = new BatchExtractorWakeUpEvent(true)

            String txIdHeader = "Transaction-Id";
            String txId = UUID.randomUUID()
            Record record = new Record()

            Message<?> message = MessageBuilder.withPayload(record).setHeader(txIdHeader, txId).build()

        when:
            transformedResultsPersister.persist(message)

        then:
            1 * mockedRecordRepository.store(record)
            1 * mockedDynamicExternalSystemMessagesAwaitingRepository.deleteByTransactionIdEquals(txId)
            1 * mockedNamesLookupRepository.findNamesLookupByTransactionIdEquals(txId) >> nameLookup
            1 * mockedNamesLookupRepository.save(nameLookup)
            1 * mockedEventBus.post(event)
            0 * _

        and:
            noExceptionThrown()
    }
}
