package com.eresearch.repositorer.repository;

import com.eresearch.repositorer.domain.externalsystem.DynamicExternalSystemMessagesAwaiting;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface DynamicExternalSystemMessagesAwaitingRepository
        extends MongoRepository<DynamicExternalSystemMessagesAwaiting, String> {

    DynamicExternalSystemMessagesAwaiting findByTransactionIdEqualsAndExternalSystemNameEquals(String transactionId, String externalSystemName);

    void deleteByTransactionIdEquals(String transactionId);
}
