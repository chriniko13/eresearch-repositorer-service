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
public class ScienceDirectSearchViewLink {

    @JsonProperty("@_fa")
    private String fa;

    @JsonProperty("@href")
    private String href;

    @JsonProperty("@ref")
    private String ref;

    @JsonProperty("@type")
    private String type;
}
