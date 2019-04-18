package com.eresearch.repositorer.dto.dblp.response;


public class DblpQueueResultDto {

    private String transactionId;
    private String exceptionMessage;
    private DblpResultsDto dblpResultsDto;

    public DblpQueueResultDto() {
    }

    public DblpQueueResultDto(String transactionId, String exceptionMessage, DblpResultsDto dblpResultsDto) {
        this.transactionId = transactionId;
        this.exceptionMessage = exceptionMessage;
        this.dblpResultsDto = dblpResultsDto;
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

    public DblpResultsDto getDblpResultsDto() {
        return dblpResultsDto;
    }

    public void setDblpResultsDto(DblpResultsDto dblpResultsDto) {
        this.dblpResultsDto = dblpResultsDto;
    }
}
