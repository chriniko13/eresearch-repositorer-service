package com.eresearch.repositorer.transformer.results.dblp.processor.authors;

import com.eresearch.repositorer.domain.record.Author;
import com.eresearch.repositorer.transformer.results.dblp.processor.common.CommonDblpSource;
import com.eresearch.repositorer.transformer.results.dblp.processor.common.ObjectAcceptor;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public interface AuthorsExtractor {

    default Set<Author> extractAuthors(Object source) {

        CommonDblpSource commonDblpSource = ObjectAcceptor.isAcceptedObject(source);

        Set<com.eresearch.repositorer.domain.record.Author> entryAuthors
                = new LinkedHashSet<>();

        List<com.eresearch.repositorer.dto.dblp.response.generated.Author> authors = commonDblpSource.getAuthors();

        if (authors != null && !authors.isEmpty()) {

            for (com.eresearch.repositorer.dto.dblp.response.generated.Author author : authors) {

                String authorFullName = author.getValue();

                String[] splittedAuthorFullName = authorFullName.split(" ");

                if (splittedAuthorFullName.length == 2) {

                    entryAuthors.add(new com.eresearch.repositorer.domain.record.Author(
                            splittedAuthorFullName[0],
                            null,
                            splittedAuthorFullName[1]));

                } else if (splittedAuthorFullName.length == 3) {

                    entryAuthors.add(new com.eresearch.repositorer.domain.record.Author(
                            splittedAuthorFullName[0],
                            splittedAuthorFullName[1],
                            splittedAuthorFullName[2]));

                } else {

                    entryAuthors.add(new com.eresearch.repositorer.domain.record.Author(
                            null,
                            null,
                            authorFullName));
                }
            }
        }
        return entryAuthors;
    }

}
