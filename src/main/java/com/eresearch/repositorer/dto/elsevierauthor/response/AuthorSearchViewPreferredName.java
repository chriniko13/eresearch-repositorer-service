package com.eresearch.repositorer.dto.elsevierauthor.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthorSearchViewPreferredName {

    @JsonProperty("surname")
    private String surname;

    @JsonProperty("given-name")
    private String givenName;

    @JsonProperty("initials")
    private String initials;

    public AuthorSearchViewPreferredName(String surname, String givenName, String initials) {
        this.surname = surname;
        this.givenName = givenName;
        this.initials = initials;
    }

    public AuthorSearchViewPreferredName() {
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }
}
