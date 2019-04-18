package com.eresearch.repositorer.domain.externalsystem;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * This domain class holds external system integration info, such as for how many messages aggregator should wait (release strategy) etc.
 */

@Getter
@Setter

@Document(collection = "dynamic-external-systems-messages-awaiting")
public class DynamicExternalSystemMessagesAwaiting {

    @Id
    private String id;

    private String transactionId;
    private String externalSystemName;
    private Integer noOfMessagesAwaiting;

    private Instant createdAt;

    public DynamicExternalSystemMessagesAwaiting(String transactionId, String externalSystemName, Integer noOfMessagesAwaiting, Instant createdAt) {
        this.transactionId = transactionId;
        this.externalSystemName = externalSystemName;
        this.noOfMessagesAwaiting = noOfMessagesAwaiting;
        this.createdAt = createdAt;
    }
}
