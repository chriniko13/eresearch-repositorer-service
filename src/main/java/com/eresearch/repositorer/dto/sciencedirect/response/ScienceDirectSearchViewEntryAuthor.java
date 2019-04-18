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
public class ScienceDirectSearchViewEntryAuthor {

    @JsonProperty("@_fa")
    private String fa;

    @JsonProperty("given-name")
    private String givenname;

    @JsonProperty("surname")
    private String surname;

}
