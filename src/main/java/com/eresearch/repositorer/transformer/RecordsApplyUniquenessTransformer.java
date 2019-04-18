package com.eresearch.repositorer.transformer;

import com.eresearch.repositorer.domain.record.Entry;
import com.eresearch.repositorer.domain.record.Record;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Log4j
@Component
public class RecordsApplyUniquenessTransformer {

    private static final String TRANSACTION_ID = "Transaction-Id";

    @Value("${apply.uniqueness.on.record.title}")
    private String applyUniquenessOnRecordTitle;

    public Message<?> transform(Message<?> message) {

        log.info("RecordsApplyUniquenessTransformer --> " + Thread.currentThread().getName());

        Record record = (Record) message.getPayload();
        String transactionId = (String) message.getHeaders().get(TRANSACTION_ID);

        if (Boolean.valueOf(applyUniquenessOnRecordTitle)) {
            Collection<Entry> entries = record.getEntries();

            if (entries != null && !entries.isEmpty()) {
                Set<Entry> uniqueEntries = new LinkedHashSet<>(entries);
                record.setEntries(uniqueEntries);
            }
        }

        return MessageBuilder
                .withPayload(record)
                .setHeader(TRANSACTION_ID, transactionId)
                .build();
    }

}
