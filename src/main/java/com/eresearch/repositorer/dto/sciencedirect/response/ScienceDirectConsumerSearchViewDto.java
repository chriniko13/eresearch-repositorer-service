package com.eresearch.repositorer.dto.sciencedirect.response;


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
public class ScienceDirectConsumerSearchViewDto {

    @JsonProperty("opensearch:totalResults")
    private String totalResults;

    @JsonProperty("opensearch:startIndex")
    private String startIndex;

    @JsonProperty("opensearch:itemsPerPage")
    private String itemsPerPage;

    @JsonProperty("opensearch:Query")
    private ScienceDirectSearchViewQuery query;

    @JsonProperty("link")
    private Collection<ScienceDirectSearchViewLink> links;

    @JsonProperty("entry")
    private Collection<ScienceDirectSearchViewEntry> entries;
}
