package com.eresearch.repositorer.transformer.results.dblp.processor;

import com.eresearch.repositorer.domain.record.Entry;
import com.eresearch.repositorer.dto.dblp.response.DblpAuthor;
import com.eresearch.repositorer.dto.dblp.response.generated.Dblp;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DblpPersonsProcessor implements DblpProcessor {

    @Override
    public void doProcessing(List<Entry> entries, Dblp dblp, DblpAuthor dblpAuthor) {
        //Note: for the moment we don't want to process persons information.
    }

    @Override
    public boolean allowProcessing() {
        return false; //Note: for the moment we don't want to process persons information.
    }
}
