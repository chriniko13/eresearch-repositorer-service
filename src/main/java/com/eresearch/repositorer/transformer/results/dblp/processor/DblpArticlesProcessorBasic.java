package com.eresearch.repositorer.transformer.results.dblp.processor;

import com.eresearch.repositorer.domain.record.Entry;
import com.eresearch.repositorer.domain.record.metadata.MetadataLabelsHolder;
import com.eresearch.repositorer.dto.dblp.response.DblpAuthor;
import com.eresearch.repositorer.dto.dblp.response.generated.Article;
import com.eresearch.repositorer.dto.dblp.response.generated.Dblp;
import com.eresearch.repositorer.exception.business.RepositorerBusinessException;
import com.eresearch.repositorer.exception.error.RepositorerError;
import com.eresearch.repositorer.transformer.results.dblp.processor.common.EntryCreator;
import com.eresearch.repositorer.transformer.results.dblp.processor.metadata.ExtraMetadataProcessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class DblpArticlesProcessorBasic implements DblpProcessor, ExtraMetadataProcessor {

    private final EntryCreator entryCreator;

    @Autowired
    public DblpArticlesProcessorBasic(EntryCreator entryCreator) {
        this.entryCreator = entryCreator;
    }

    @Override
    public void doProcessing(List<Entry> entries, Dblp dblp, DblpAuthor dblpAuthor) throws JsonProcessingException {

        List<Article> articles = dblp.getArticles();

        if (articles != null && !articles.isEmpty()) {

            final List<Entry> collectedEntries = new LinkedList<>();

            for (Article article : articles) {

                entryCreator.create(dblpAuthor, collectedEntries, article, this);
            }

            entries.addAll(collectedEntries);
        }
    }

    @Override
    public void extraMetadataPopulation(Object source, Map<String, String> metadata) {

        if (!(source instanceof Article)) {
            throw new RepositorerBusinessException(RepositorerError.APPLICATION_NOT_IN_CORRECT_STATE,
                    RepositorerError.APPLICATION_NOT_IN_CORRECT_STATE.getMessage(),
                    this.getClass().getName());
        }

        Article article = (Article) source;

        String reviewid = article.getReviewid();
        metadata.put(MetadataLabelsHolder.DblpLabels.REVIEW_ID.getLabelName(),
                reviewid);

        String rating = article.getRating();
        metadata.put(MetadataLabelsHolder.DblpLabels.RATING.getLabelName(),
                rating);
    }

    @Override
    public boolean allowProcessing() {
        return true;
    }
}
