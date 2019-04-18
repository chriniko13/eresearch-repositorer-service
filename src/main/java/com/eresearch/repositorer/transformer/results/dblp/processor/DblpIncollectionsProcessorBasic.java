package com.eresearch.repositorer.transformer.results.dblp.processor;

import com.eresearch.repositorer.domain.record.Entry;
import com.eresearch.repositorer.dto.dblp.response.DblpAuthor;
import com.eresearch.repositorer.dto.dblp.response.generated.Dblp;
import com.eresearch.repositorer.dto.dblp.response.generated.Incollection;
import com.eresearch.repositorer.transformer.results.dblp.processor.common.EntryCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class DblpIncollectionsProcessorBasic implements DblpProcessor {

    private final EntryCreator entryCreator;

    @Autowired
    public DblpIncollectionsProcessorBasic(EntryCreator entryCreator) {
        this.entryCreator = entryCreator;
    }

    @Override
    public void doProcessing(List<Entry> entries, Dblp dblp, DblpAuthor dblpAuthor) throws JsonProcessingException {

        List<Incollection> incollections = dblp.getIncollections();

        if (incollections != null && !incollections.isEmpty()) {

            final List<Entry> collectedEntries = new LinkedList<>();

            for (Incollection incollection : incollections) {

                entryCreator.create(dblpAuthor, collectedEntries, incollection);
            }

            entries.addAll(collectedEntries);
        }
    }

    @Override
    public boolean allowProcessing() {
        return true;
    }
}
