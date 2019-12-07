package com.eresearch.repositorer.dto.scopus.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScopusSearchAuthorCount {

    @JsonProperty("@limit")
    private String limit;

    @JsonProperty("$")
    private String authorCount;

    @JsonProperty("@total")
    private String total;
}
