package com.eresearch.repositorer.dto.sciencedirect.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScienceDirectSearchViewEntryAuthors {

    @JsonProperty("author")
    private Collection<ScienceDirectSearchViewEntryAuthor> authors;

    @JsonProperty("collaboration")
    private List<ScienceDirectSearchViewEntryCollaboration> collaborations;
}
