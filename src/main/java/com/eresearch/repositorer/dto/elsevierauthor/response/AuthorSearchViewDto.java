package com.eresearch.repositorer.dto.elsevierauthor.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class AuthorSearchViewDto {

    @JsonProperty("opensearch:totalResults")
    private String totalResults;

    @JsonProperty("opensearch:startIndex")
    private String startIndex;

    @JsonProperty("opensearch:itemsPerPage")
    private String itemsPerPage;

    @JsonProperty("opensearch:Query")
    private AuthorSearchViewQuery query;

    @JsonProperty("link")
    private Collection<AuthorSearchViewLink> links;

    @JsonProperty("entry")
    private Collection<AuthorSearchViewEntry> entries;

    public AuthorSearchViewDto() {
    }

    public AuthorSearchViewDto(String totalResults, String startIndex, String itemsPerPage, AuthorSearchViewQuery query, Collection<AuthorSearchViewLink> links, Collection<AuthorSearchViewEntry> entries) {
        this.totalResults = totalResults;
        this.startIndex = startIndex;
        this.itemsPerPage = itemsPerPage;
        this.query = query;
        this.links = links;
        this.entries = entries;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(String totalResults) {
        this.totalResults = totalResults;
    }

    public String getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(String startIndex) {
        this.startIndex = startIndex;
    }

    public String getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(String itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public AuthorSearchViewQuery getQuery() {
        return query;
    }

    public void setQuery(AuthorSearchViewQuery query) {
        this.query = query;
    }

    public Collection<AuthorSearchViewLink> getLinks() {
        return links;
    }

    public void setLinks(Collection<AuthorSearchViewLink> links) {
        this.links = links;
    }

    public Collection<AuthorSearchViewEntry> getEntries() {
        return entries;
    }

    public void setEntries(Collection<AuthorSearchViewEntry> entries) {
        this.entries = entries;
    }
}
