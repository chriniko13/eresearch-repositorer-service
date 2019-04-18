package com.eresearch.repositorer.dto.elsevierauthor.response;

import com.fasterxml.jackson.annotation.JsonProperty;


public class AuthorSearchViewSubjectArea {

    @JsonProperty("@abbrev")
    private String abbrev;

    @JsonProperty("@frequency")
    private String frequency;

    @JsonProperty("$")
    private String subjectArea;

    public AuthorSearchViewSubjectArea() {
    }

    public AuthorSearchViewSubjectArea(String abbrev, String frequency, String subjectArea) {
        this.abbrev = abbrev;
        this.frequency = frequency;
        this.subjectArea = subjectArea;
    }

    public String getAbbrev() {
        return abbrev;
    }

    public void setAbbrev(String abbrev) {
        this.abbrev = abbrev;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getSubjectArea() {
        return subjectArea;
    }

    public void setSubjectArea(String subjectArea) {
        this.subjectArea = subjectArea;
    }
}
