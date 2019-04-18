package com.eresearch.repositorer.dto.sciencedirect.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScienceDirectSearchViewEntryCollaboration {

    @JsonProperty("@_fa")
    private String fa;

    @JsonProperty("$")
    private String value;
}
