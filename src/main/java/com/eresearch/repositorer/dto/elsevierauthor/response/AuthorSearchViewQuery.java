package com.eresearch.repositorer.dto.elsevierauthor.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class AuthorSearchViewQuery {

    @JsonProperty("@role")
    private String role;

    @JsonProperty("@searchTerms")
    private String searchTerms;

    @JsonProperty("@startPage")
    private String startPage;

    public AuthorSearchViewQuery() {
    }

    public AuthorSearchViewQuery(String role, String searchTerms, String startPage) {
        this.role = role;
        this.searchTerms = searchTerms;
        this.startPage = startPage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AuthorSearchViewQuery that = (AuthorSearchViewQuery) o;
        return Objects.equals(role, that.role) &&
                Objects.equals(searchTerms, that.searchTerms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), role, searchTerms);
    }

    @Override
    public String toString() {
        return "AuthorSearchViewQuery{" +
                "role='" + role + '\'' +
                ", searchTerms='" + searchTerms + '\'' +
                '}';
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSearchTerms() {
        return searchTerms;
    }

    public void setSearchTerms(String searchTerms) {
        this.searchTerms = searchTerms;
    }

    public String getStartPage() {
        return startPage;
    }

    public void setStartPage(String startPage) {
        this.startPage = startPage;
    }
}
