package com.eresearch.repositorer.dto.sciencedirect.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScienceDirectConsumerResultsDto {

    @JsonProperty("search-results")
    private ScienceDirectConsumerSearchViewDto scienceDirectConsumerSearchViewDto;

}
