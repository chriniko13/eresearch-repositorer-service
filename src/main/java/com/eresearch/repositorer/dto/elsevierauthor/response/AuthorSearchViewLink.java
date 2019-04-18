package com.eresearch.repositorer.dto.elsevierauthor.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthorSearchViewLink {

    @JsonProperty("@_fa")
    private String fa;

    @JsonProperty("@href")
    private String href;

    @JsonProperty("@ref")
    private String ref;

    @JsonProperty("@type")
    private String type;

    public AuthorSearchViewLink() {
    }

    public AuthorSearchViewLink(String fa, String href, String ref, String type) {
        this.fa = fa;
        this.href = href;
        this.ref = ref;
        this.type = type;
    }

    public String getFa() {
        return fa;
    }

    public void setFa(String fa) {
        this.fa = fa;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
