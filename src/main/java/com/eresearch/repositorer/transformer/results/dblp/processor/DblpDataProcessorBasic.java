package com.eresearch.repositorer.transformer.results.dblp.processor;

import com.eresearch.repositorer.domain.record.Entry;
import com.eresearch.repositorer.dto.dblp.response.DblpAuthor;
import com.eresearch.repositorer.dto.dblp.response.generated.Data;
import com.eresearch.repositorer.dto.dblp.response.generated.Dblp;
import com.eresearch.repositorer.transformer.results.dblp.processor.common.EntryCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class DblpDataProcessorBasic implements DblpProcessor {

    private final EntryCreator entryCreator;

    @Autowired
    public DblpDataProcessorBasic(EntryCreator entryCreator) {
        this.entryCreator = entryCreator;
    }

    @Override
    public void doProcessing(List<Entry> entries, Dblp dblp, DblpAuthor dblpAuthor) throws JsonProcessingException {

        List<Data> data = dblp.getData();

        if (data != null && !data.isEmpty()) {

            final List<Entry> collectedEntries = new LinkedList<>();

            for (Data datum : data) {

                entryCreator.create(dblpAuthor, collectedEntries, datum);
            }

            entries.addAll(collectedEntries);
        }
    }

    @Override
    public boolean allowProcessing() {
        return false;
    }
}
