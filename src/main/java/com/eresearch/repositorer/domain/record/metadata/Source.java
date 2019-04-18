package com.eresearch.repositorer.domain.record.metadata;

public enum Source {

    ELSEVIER_SCOPUS("Elsevier Scopus"),
    SCIENCE_DIRECT("Science Direct"),
    DBLP("DBLP");

    private final String sourceName;

    Source(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceName() {
        return sourceName;
    }
}
