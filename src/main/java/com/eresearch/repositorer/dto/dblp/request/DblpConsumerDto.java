package com.eresearch.repositorer.dto.dblp.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class DblpConsumerDto {

    @JsonProperty("firstname")
    private String firstname;

    @JsonProperty("initials")
    private String initials;

    @JsonProperty("surname")
    private String surname;

    public DblpConsumerDto() {
    }

    public DblpConsumerDto(String firstname, String initials, String surname) {
        this.firstname = firstname;
        this.initials = initials;
        this.surname = surname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
