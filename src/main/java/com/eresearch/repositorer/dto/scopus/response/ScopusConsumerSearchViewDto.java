package com.eresearch.repositorer.dto.scopus.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScopusConsumerSearchViewDto {

    @JsonProperty("opensearch:totalResults")
    private String totalResults;

    @JsonProperty("opensearch:startIndex")
    private String startIndex;

    @JsonProperty("opensearch:itemsPerPage")
    private String itemsPerPage;

    @JsonProperty("opensearch:Query")
    private ScopusSearchViewQuery query;

    @JsonProperty("link")
    private Collection<ScopusSearchViewLink> links;

    @JsonProperty("entry")
    private Collection<ScopusSearchViewEntry> entries;
}
