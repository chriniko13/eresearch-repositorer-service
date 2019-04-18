package com.eresearch.repositorer.transformer.results.dblp.processor;

import com.eresearch.repositorer.domain.record.Entry;
import com.eresearch.repositorer.dto.dblp.response.DblpAuthor;
import com.eresearch.repositorer.dto.dblp.response.generated.Dblp;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface DblpProcessor {

    void doProcessing(List<Entry> entries, Dblp dblp, DblpAuthor dblpAuthor) throws JsonProcessingException;

    boolean allowProcessing();
}
