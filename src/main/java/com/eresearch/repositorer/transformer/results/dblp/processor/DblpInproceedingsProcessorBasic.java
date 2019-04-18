package com.eresearch.repositorer.transformer.results.dblp.processor;

import com.eresearch.repositorer.domain.record.Entry;
import com.eresearch.repositorer.dto.dblp.response.DblpAuthor;
import com.eresearch.repositorer.dto.dblp.response.generated.Dblp;
import com.eresearch.repositorer.dto.dblp.response.generated.Inproceedings;
import com.eresearch.repositorer.transformer.results.dblp.processor.common.EntryCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class DblpInproceedingsProcessorBasic implements DblpProcessor {

    private final EntryCreator entryCreator;

    @Autowired
    public DblpInproceedingsProcessorBasic(EntryCreator entryCreator) {
        this.entryCreator = entryCreator;
    }

    @Override
    public void doProcessing(List<Entry> entries, Dblp dblp, DblpAuthor dblpAuthor) throws JsonProcessingException {

        List<Inproceedings> inproceedings = dblp.getInproceedings();

        if (inproceedings != null && !inproceedings.isEmpty()) {

            final List<Entry> collectedEntries = new LinkedList<>();

            for (Inproceedings inproceeding : inproceedings) {

                entryCreator.create(dblpAuthor, collectedEntries, inproceeding);

            }

            entries.addAll(collectedEntries);
        }
    }

    @Override
    public boolean allowProcessing() {
        return true;
    }
}

