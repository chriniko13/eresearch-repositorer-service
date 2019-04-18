package com.eresearch.repositorer.dto.scopus.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@ToString(of = {"role", "searchTerms"})
@EqualsAndHashCode(of = {"role", "searchTerms"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScopusSearchViewQuery {

    @JsonProperty("@role")
    private String role;

    @JsonProperty("@searchTerms")
    private String searchTerms;

    @JsonProperty("@startPage")
    private String startPage;

}
