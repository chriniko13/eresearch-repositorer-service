package com.eresearch.repositorer.dto.elsevierauthor.response;

import com.fasterxml.jackson.annotation.JsonProperty;


public class AuthorSearchViewAffiliationCurrent {

    @JsonProperty("affiliation-url")
    private String url;

    @JsonProperty("affiliation-id")
    private String id;

    @JsonProperty("affiliation-name")
    private String name;

    @JsonProperty("affiliation-city")
    private String city;

    @JsonProperty("affiliation-country")
    private String country;

    public AuthorSearchViewAffiliationCurrent() {
    }

    public AuthorSearchViewAffiliationCurrent(String url, String id, String name, String city, String country) {
        this.url = url;
        this.id = id;
        this.name = name;
        this.city = city;
        this.country = country;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
