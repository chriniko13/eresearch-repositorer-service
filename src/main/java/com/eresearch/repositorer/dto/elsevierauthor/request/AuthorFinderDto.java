package com.eresearch.repositorer.dto.elsevierauthor.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;


@Builder
@ToString
public class AuthorFinderDto {

    @JsonProperty("author-name")
    private AuthorNameDto authorName;

    public AuthorFinderDto() {
    }

    public AuthorFinderDto(AuthorNameDto authorName) {
        this.authorName = authorName;
    }

    public AuthorNameDto getAuthorName() {
        return authorName;
    }

    public void setAuthorName(AuthorNameDto authorName) {
        this.authorName = authorName;
    }
}
