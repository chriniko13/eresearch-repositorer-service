package com.eresearch.repositorer.transformer.results.dblp.processor;

import com.eresearch.repositorer.domain.record.Entry;
import com.eresearch.repositorer.dto.dblp.response.DblpAuthor;
import com.eresearch.repositorer.dto.dblp.response.generated.Dblp;
import com.eresearch.repositorer.dto.dblp.response.generated.Proceedings;
import com.eresearch.repositorer.transformer.results.dblp.processor.common.EntryCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class DblpProceedingsProcessorBasic implements DblpProcessor {

    private final EntryCreator entryCreator;

    @Autowired
    public DblpProceedingsProcessorBasic(EntryCreator entryCreator) {
        this.entryCreator = entryCreator;
    }

    @Override
    public void doProcessing(List<Entry> entries, Dblp dblp, DblpAuthor dblpAuthor) throws JsonProcessingException {

        List<Proceedings> proceedings = dblp.getProceedings();

        if (proceedings != null && !proceedings.isEmpty()) {

            final List<Entry> collectedEntries = new LinkedList<>();

            for (Proceedings proceeding : proceedings) {

                entryCreator.create(dblpAuthor, collectedEntries, proceeding);

            }

            entries.addAll(collectedEntries);
        }
    }

    @Override
    public boolean allowProcessing() {
        return true;
    }
}
