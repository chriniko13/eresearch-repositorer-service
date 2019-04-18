package com.eresearch.repositorer.dto.elsevierauthor.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthorFinderQueueResultDto {

    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("exceptionMessage")
    private String exceptionMessage;

    @JsonProperty("authorFinderResultsDto")
    private AuthorFinderResultsDto authorFinderResultsDto;

    public AuthorFinderQueueResultDto() {
    }

    public AuthorFinderQueueResultDto(String transactionId, String exceptionMessage, AuthorFinderResultsDto authorFinderResultsDto) {
        this.transactionId = transactionId;
        this.exceptionMessage = exceptionMessage;
        this.authorFinderResultsDto = authorFinderResultsDto;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public AuthorFinderResultsDto getAuthorFinderResultsDto() {
        return authorFinderResultsDto;
    }

    public void setAuthorFinderResultsDto(AuthorFinderResultsDto authorFinderResultsDto) {
        this.authorFinderResultsDto = authorFinderResultsDto;
    }
}
