package com.eresearch.repositorer.transformer.results.dblp.processor.metadata;

import java.util.Map;


public interface ExtraMetadataProcessor {

    void extraMetadataPopulation(Object source, Map<String, String> metadata);

}
