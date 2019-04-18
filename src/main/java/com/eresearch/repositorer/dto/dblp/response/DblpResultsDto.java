package com.eresearch.repositorer.dto.dblp.response;


import com.eresearch.repositorer.deserializer.DblpAuthorKeyDeserializer;
import com.eresearch.repositorer.dto.dblp.request.DblpConsumerDto;
import com.eresearch.repositorer.dto.dblp.response.generated.Dblp;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class DblpResultsDto {

    @JsonProperty("operation-result")
    private Boolean operationResult;

    @JsonProperty("process-finished-date")
    private Instant processFinishedDate;

    @JsonProperty("requested-dblp-consumer-dto")
    private DblpConsumerDto dblpConsumerDto;

    @JsonProperty("fetched-results-size")
    private Integer resultsSize;

    @JsonProperty("fetched-results")
    @JsonDeserialize(keyUsing = DblpAuthorKeyDeserializer.class)
    private Map<DblpAuthor, List<Dblp>> results;

    public DblpResultsDto() {
    }

    public DblpResultsDto(Boolean operationResult, Instant processFinishedDate, DblpConsumerDto dblpConsumerDto, Integer resultsSize, Map<DblpAuthor, List<Dblp>> results) {
        this.operationResult = operationResult;
        this.processFinishedDate = processFinishedDate;
        this.dblpConsumerDto = dblpConsumerDto;
        this.resultsSize = resultsSize;
        this.results = results;
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

    public DblpConsumerDto getDblpConsumerDto() {
        return dblpConsumerDto;
    }

    public void setDblpConsumerDto(DblpConsumerDto dblpConsumerDto) {
        this.dblpConsumerDto = dblpConsumerDto;
    }

    public Integer getResultsSize() {
        return resultsSize;
    }

    public void setResultsSize(Integer resultsSize) {
        this.resultsSize = resultsSize;
    }

    public Map<DblpAuthor, List<Dblp>> getResults() {
        return results;
    }

    public void setResults(Map<DblpAuthor, List<Dblp>> results) {
        this.results = results;
    }
}
