package com.eresearch.repositorer.dto.elsevierauthor.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.ToString;


@Builder
@ToString
public class AuthorNameDto {

    @JsonProperty("firstname")
    private String firstName;

    @JsonProperty("initials")
    private String initials;

    @JsonProperty("surname")
    private String surname;

    public AuthorNameDto() {
    }

    public AuthorNameDto(String firstName, String initials, String surname) {
        this.firstName = firstName;
        this.initials = initials;
        this.surname = surname;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
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
