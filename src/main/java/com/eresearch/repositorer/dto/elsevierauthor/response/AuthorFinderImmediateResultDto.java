package com.eresearch.repositorer.dto.elsevierauthor.response;

import lombok.ToString;

@ToString
public class AuthorFinderImmediateResultDto {

    private String message;

    public AuthorFinderImmediateResultDto(String message) {
        this.message = message;
    }

    public AuthorFinderImmediateResultDto() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
