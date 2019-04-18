package com.eresearch.repositorer.transformer.results.metadata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProcessMetadataPredicate {

    @Value("${collect.metadata.info}")
    private String doMetadataProcessing;

    public boolean doMetadataProcessing() {
        return Boolean.valueOf(doMetadataProcessing);
    }
}
