package com.eresearch.repositorer.dto.dblp.response;

public class DblpImmediateResultDto {

    private String message;

    public DblpImmediateResultDto() {
    }

    public DblpImmediateResultDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "DblpImmediateResultDto{" +
                "message='" + message + '\'' +
                '}';
    }
}
