package com.eresearch.repositorer.transformer.results.metadata;

import com.eresearch.repositorer.domain.record.Record;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Log4j
public class MetadataNullValuesRemoverTransformer {

    @Value("${metadata.remove.null.values}")
    private String removeNullValuesFromMetadata;

    public Message<?> transform(Message<?> message) {

        log.info("MetadataNullValuesRemoverTransformer --> " + Thread.currentThread().getName());

        log.info("MetadataNullValuesRemoverTransformer#transform --- message = " + message);

        if (Boolean.valueOf(removeNullValuesFromMetadata)) {
            final Record record = (Record) message.getPayload();
            record.getEntries().forEach(entry -> removeNullValues(entry.getMetadata()));
        }

        return message;
    }

    private void removeNullValues(Map<String, String> metadata) {
        metadata.entrySet().removeIf(metadataEntry -> metadataEntry.getValue() == null);
    }
}
