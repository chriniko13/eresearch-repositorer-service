package com.eresearch.repositorer.dto.elsevierauthor.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import java.util.Collection;
import java.util.Objects;

@EqualsAndHashCode(of = {"dcIdentifier"})
public class AuthorSearchViewEntry {

    @JsonProperty("@force-array")
    private String forceArray;

    @JsonProperty("error")
    private String error;

    @JsonProperty("@_fa")
    private String fa;

    @JsonProperty("link")
    private Collection<AuthorSearchViewLink> links;

    @JsonProperty("prism:url")
    private String prismUrl;

    @JsonProperty("dc:identifier")
    private String dcIdentifier;

    @JsonProperty("eid")
    private String eid;

    @JsonProperty("orcid")
    private String orcId;

    @JsonProperty("preferred-name")
    private AuthorSearchViewPreferredName preferredName;

    @JsonProperty("name-variant")
    private Collection<AuthorSearchViewNameVariant> nameVariants;

    @JsonProperty("document-count")
    private String documentCount;

    @JsonProperty("subject-area")
    private Collection<AuthorSearchViewSubjectArea> subjectAreas;

    @JsonProperty("affiliation-current")
    private AuthorSearchViewAffiliationCurrent affiliationCurrent;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AuthorSearchViewEntry that = (AuthorSearchViewEntry) o;
        return Objects.equals(dcIdentifier, that.dcIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dcIdentifier);
    }

    public AuthorSearchViewEntry() {
    }

    public AuthorSearchViewEntry(String forceArray, String error, String fa, Collection<AuthorSearchViewLink> links, String prismUrl, String dcIdentifier, String eid, String orcId, AuthorSearchViewPreferredName preferredName, Collection<AuthorSearchViewNameVariant> nameVariants, String documentCount, Collection<AuthorSearchViewSubjectArea> subjectAreas, AuthorSearchViewAffiliationCurrent affiliationCurrent) {
        this.forceArray = forceArray;
        this.error = error;
        this.fa = fa;
        this.links = links;
        this.prismUrl = prismUrl;
        this.dcIdentifier = dcIdentifier;
        this.eid = eid;
        this.orcId = orcId;
        this.preferredName = preferredName;
        this.nameVariants = nameVariants;
        this.documentCount = documentCount;
        this.subjectAreas = subjectAreas;
        this.affiliationCurrent = affiliationCurrent;
    }

    public String getForceArray() {
        return forceArray;
    }

    public void setForceArray(String forceArray) {
        this.forceArray = forceArray;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getFa() {
        return fa;
    }

    public void setFa(String fa) {
        this.fa = fa;
    }

    public Collection<AuthorSearchViewLink> getLinks() {
        return links;
    }

    public void setLinks(Collection<AuthorSearchViewLink> links) {
        this.links = links;
    }

    public String getPrismUrl() {
        return prismUrl;
    }

    public void setPrismUrl(String prismUrl) {
        this.prismUrl = prismUrl;
    }

    public String getDcIdentifier() {
        return dcIdentifier;
    }

    public void setDcIdentifier(String dcIdentifier) {
        this.dcIdentifier = dcIdentifier;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getOrcId() {
        return orcId;
    }

    public void setOrcId(String orcId) {
        this.orcId = orcId;
    }

    public AuthorSearchViewPreferredName getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(AuthorSearchViewPreferredName preferredName) {
        this.preferredName = preferredName;
    }

    public Collection<AuthorSearchViewNameVariant> getNameVariants() {
        return nameVariants;
    }

    public void setNameVariants(Collection<AuthorSearchViewNameVariant> nameVariants) {
        this.nameVariants = nameVariants;
    }

    public String getDocumentCount() {
        return documentCount;
    }

    public void setDocumentCount(String documentCount) {
        this.documentCount = documentCount;
    }

    public Collection<AuthorSearchViewSubjectArea> getSubjectAreas() {
        return subjectAreas;
    }

    public void setSubjectAreas(Collection<AuthorSearchViewSubjectArea> subjectAreas) {
        this.subjectAreas = subjectAreas;
    }

    public AuthorSearchViewAffiliationCurrent getAffiliationCurrent() {
        return affiliationCurrent;
    }

    public void setAffiliationCurrent(AuthorSearchViewAffiliationCurrent affiliationCurrent) {
        this.affiliationCurrent = affiliationCurrent;
    }
}
