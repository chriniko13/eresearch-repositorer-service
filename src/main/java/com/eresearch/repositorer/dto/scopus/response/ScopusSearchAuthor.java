package com.eresearch.repositorer.dto.scopus.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScopusSearchAuthor {

    @JsonProperty("@_fa")
    private String fa;

    @JsonProperty("@seq")
    private String sequenceInEntry;

    @JsonProperty("author-url")
    private String authorUrl;

    @JsonProperty("authid")
    private String authorId;

    @JsonProperty("orcid")
    private String orcId;

    @JsonProperty("authname")
    private String authorName;

    @JsonProperty("given-name")
    private String givenName;

    @JsonProperty("surname")
    private String surname;

    @JsonProperty("initials")
    private String initials;

    @JsonProperty("afid")
    private Collection<ScopusSearchAuthorAffiliationId> affiliationIds;

}
