package com.eresearch.repositorer.dto.sciencedirect.response;


import com.eresearch.repositorer.dto.sciencedirect.request.ElsevierScienceDirectConsumerDto;
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
public class ElsevierScienceDirectConsumerResultsDto {

    @JsonProperty("operation-result")
    private Boolean operationResult;

    @JsonProperty("process-finished-date")
    private Instant processFinishedDate;

    @JsonProperty("requested-elsevier-sciencedirect-consumer-dto")
    private ElsevierScienceDirectConsumerDto requestedElsevierScienceDirectConsumerDto;

    @JsonProperty("fetched-results-size")
    private Integer resultsSize;

    @JsonProperty("fetched-results")
    private Collection<ScienceDirectConsumerResultsDto> results;

}
