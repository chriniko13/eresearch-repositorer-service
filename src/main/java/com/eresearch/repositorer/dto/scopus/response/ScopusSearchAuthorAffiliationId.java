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
public class ScopusSearchAuthorAffiliationId {

    @JsonProperty("@_fa")
    private String fa;

    @JsonProperty("$")
    private String affilationId;

}
