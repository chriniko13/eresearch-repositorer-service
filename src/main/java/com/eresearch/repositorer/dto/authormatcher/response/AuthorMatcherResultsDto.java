package com.eresearch.repositorer.dto.authormatcher.response;

import com.eresearch.repositorer.dto.authormatcher.request.AuthorComparisonDto;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Map;


public class AuthorMatcherResultsDto {

    @JsonProperty("operation-result")
    private Boolean operationResult;

    @JsonProperty("process-finished-date")
    private Instant processFinishedDate;

    @JsonProperty("comparison-results")
    private Map<StringMetricAlgorithm, StringMetricResultDto> results;

    @JsonProperty("comparison-input")
    private AuthorComparisonDto authorComparisonDto;

    public AuthorMatcherResultsDto() {
    }

    public AuthorMatcherResultsDto(Boolean operationResult, Instant processFinishedDate, Map<StringMetricAlgorithm, StringMetricResultDto> results, AuthorComparisonDto authorComparisonDto) {
        this.operationResult = operationResult;
        this.processFinishedDate = processFinishedDate;
        this.results = results;
        this.authorComparisonDto = authorComparisonDto;
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

    public Map<StringMetricAlgorithm, StringMetricResultDto> getResults() {
        return results;
    }

    public void setResults(Map<StringMetricAlgorithm, StringMetricResultDto> results) {
        this.results = results;
    }

    public AuthorComparisonDto getAuthorComparisonDto() {
        return authorComparisonDto;
    }

    public void setAuthorComparisonDto(AuthorComparisonDto authorComparisonDto) {
        this.authorComparisonDto = authorComparisonDto;
    }
}
