package com.eresearch.repositorer.dto.authormatcher.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StringMetricResultDto {

    @JsonProperty("comparison-result")
    private Double comparisonResult;

    @JsonProperty("comparison-result-floor")
    private Double comparisonResultFloor;

    @JsonProperty("comparison-result-ceil")
    private Double comparisonResultCeil;

    public StringMetricResultDto() {
    }

    public StringMetricResultDto(Double comparisonResult, Double comparisonResultFloor, Double comparisonResultCeil) {
        this.comparisonResult = comparisonResult;
        this.comparisonResultFloor = comparisonResultFloor;
        this.comparisonResultCeil = comparisonResultCeil;
    }

    public Double getComparisonResult() {
        return comparisonResult;
    }

    public void setComparisonResult(Double comparisonResult) {
        this.comparisonResult = comparisonResult;
    }

    public Double getComparisonResultFloor() {
        return comparisonResultFloor;
    }

    public void setComparisonResultFloor(Double comparisonResultFloor) {
        this.comparisonResultFloor = comparisonResultFloor;
    }

    public Double getComparisonResultCeil() {
        return comparisonResultCeil;
    }

    public void setComparisonResultCeil(Double comparisonResultCeil) {
        this.comparisonResultCeil = comparisonResultCeil;
    }
}
