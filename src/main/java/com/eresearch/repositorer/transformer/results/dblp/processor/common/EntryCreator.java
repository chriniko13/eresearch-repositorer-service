package com.eresearch.repositorer.transformer.results.dblp.processor.common;


import com.eresearch.repositorer.domain.record.Author;
import com.eresearch.repositorer.domain.record.Entry;
import com.eresearch.repositorer.dto.dblp.response.DblpAuthor;
import com.eresearch.repositorer.dto.dblp.response.generated.Title;
import com.eresearch.repositorer.transformer.results.dblp.processor.authors.AuthorsExtractor;
import com.eresearch.repositorer.transformer.results.dblp.processor.metadata.BasicMetadataProcessor;
import com.eresearch.repositorer.transformer.results.dblp.processor.metadata.ExtraMetadataProcessor;
import com.eresearch.repositorer.transformer.results.metadata.ProcessMetadataPredicate;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class EntryCreator implements AuthorsExtractor, ObjectAcceptor {

    private static final int DEFAULT_TITLE_INDEX_TO_USE = 0;

    private final BasicMetadataProcessor basicMetadataProcessor;
    private final ProcessMetadataPredicate processMetadataPredicate;

    @Autowired
    public EntryCreator(BasicMetadataProcessor basicMetadataProcessor, ProcessMetadataPredicate processMetadataPredicate) {
        this.basicMetadataProcessor = basicMetadataProcessor;
        this.processMetadataPredicate = processMetadataPredicate;
    }

    public void create(DblpAuthor dblpAuthor, List<Entry> collectedEntries, Object source, ExtraMetadataProcessor... extraMetadataProcessors) throws JsonProcessingException {

        final Map<String, String> metadata;
        if (processMetadataPredicate.doMetadataProcessing()) {
            metadata = basicMetadataProcessor.metadataPopulation(source, dblpAuthor);
        } else {
            metadata = null;
        }

        //if provided do the extra metadata processing...
        if (extraMetadataProcessors != null) {
            for (ExtraMetadataProcessor extraMetadataProcessor : Arrays.asList(extraMetadataProcessors)) {
                extraMetadataProcessor.extraMetadataPopulation(source, metadata);
            }
        }

        List<Title> titles = ObjectAcceptor.isAcceptedObject(source).getTitles();
        String title = null;
        if (titles != null && !titles.isEmpty()) {
            title = titles.get(DEFAULT_TITLE_INDEX_TO_USE).getValue();
        }

        Set<Author> entryAuthors
                = extractAuthors(source);

        Entry entry = Entry.builder()
                .title(title)
                .authors(entryAuthors)
                .metadata(metadata)
                .build();

        collectedEntries.add(entry);
    }

}
