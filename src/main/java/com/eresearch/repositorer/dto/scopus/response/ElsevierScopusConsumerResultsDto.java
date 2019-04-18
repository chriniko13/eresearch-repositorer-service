package com.eresearch.repositorer.dto.scopus.response;


import com.eresearch.repositorer.dto.scopus.request.ElsevierScopusConsumerDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ElsevierScopusConsumerResultsDto {

    @JsonProperty("operation-result")
    private Boolean operationResult;

    @JsonProperty("process-finished-date")
    private Instant processFinishedDate;

    @JsonProperty("requested-elsevier-scopus-consumer-dto")
    private ElsevierScopusConsumerDto requestedElsevierScopusConsumerDto;

    @JsonProperty("fetched-results-size")
    private Integer resultsSize;

    @JsonProperty("fetched-results")
    private Collection<ScopusConsumerResultsDto> results;
}
