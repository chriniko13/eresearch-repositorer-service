package com.eresearch.repositorer.dto.elsevierauthor.response;

import com.eresearch.repositorer.dto.elsevierauthor.request.AuthorFinderDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Collection;

public class AuthorFinderResultsDto {

    @JsonProperty("operation-result")
    private Boolean operationResult;

    @JsonProperty("process-finished-date")
    private Instant processFinishedDate;

    @JsonProperty("requested-author-finder-dto")
    private AuthorFinderDto requestedAuthorFinderDto;

    @JsonProperty("fetched-results-size")
    private Integer resultsSize;

    @JsonProperty("fetched-results")
    private Collection<AuthorSearchViewResultsDto> results;

    public AuthorFinderResultsDto(Boolean operationResult, Instant processFinishedDate, AuthorFinderDto requestedAuthorFinderDto, Integer resultsSize, Collection<AuthorSearchViewResultsDto> results) {
        this.operationResult = operationResult;
        this.processFinishedDate = processFinishedDate;
        this.requestedAuthorFinderDto = requestedAuthorFinderDto;
        this.resultsSize = resultsSize;
        this.results = results;
    }

    public AuthorFinderResultsDto() {
    }

    public Boolean getOperationResult() {
        return operationResult;
    }

    public void setOperationResult(Boolean operationResult) {
        this.operationResult = operationResult;
    }

    public Instant getProcessFinishedDate() {
        return processFinishedDate;
    }

    public void setProcessFinishedDate(Instant processFinishedDate) {
        this.processFinishedDate = processFinishedDate;
    }

    public AuthorFinderDto getRequestedAuthorFinderDto() {
        return requestedAuthorFinderDto;
    }

    public void setRequestedAuthorFinderDto(AuthorFinderDto requestedAuthorFinderDto) {
        this.requestedAuthorFinderDto = requestedAuthorFinderDto;
    }

    public Integer getResultsSize() {
        return resultsSize;
    }

    public void setResultsSize(Integer resultsSize) {
        this.resultsSize = resultsSize;
    }

    public Collection<AuthorSearchViewResultsDto> getResults() {
        return results;
    }

    public void setResults(Collection<AuthorSearchViewResultsDto> results) {
        this.results = results;
    }
}
