package com.eresearch.repositorer.dto.elsevierauthor.response;

import com.fasterxml.jackson.annotation.JsonProperty;


public class AuthorSearchViewResultsDto {

    @JsonProperty("search-results")
    private AuthorSearchViewDto authorSearchViewDto;

    public AuthorSearchViewResultsDto(AuthorSearchViewDto authorSearchViewDto) {
        this.authorSearchViewDto = authorSearchViewDto;
    }

    public AuthorSearchViewResultsDto() {
    }

    public AuthorSearchViewDto getAuthorSearchViewDto() {
        return authorSearchViewDto;
    }

    public void setAuthorSearchViewDto(AuthorSearchViewDto authorSearchViewDto) {
        this.authorSearchViewDto = authorSearchViewDto;
    }
}
