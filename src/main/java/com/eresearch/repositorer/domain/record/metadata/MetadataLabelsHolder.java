package com.eresearch.repositorer.domain.record.metadata;

public enum MetadataLabelsHolder {

    SOURCE("Source");

    public enum ScopusLabels {

        LINKS("Links"),
        PRISM_URL("Prism Url"),
        DC_IDENTIFIER("Dc Identifier"),
        EID("Eid"),
        DC_TITLE("Dc Title"),
        PRISM_AGGREGATION_TYPE("Prism Aggregation Type"),
        CITED_BY_COUNT("Cited by count"),
        PRISM_PUBLICATION_NAME("Prism Publication Name"),
        PRISM_ISBNS("Prism Isbns"),
        PRISM_ISSN("Prism Issn"),
        PRISM_EISSN("Prism EIssn"),
        PRISM_VOLUME("Prism Volume"),
        PRISM_ISSUE_IDENTIFIER("Prism Issue Identifier"),
        PRISM_PAGE_RANGE("Prism Page Range"),
        PRISM_COVER_DATE("Prism Cover Date"),
        PRISM_COVER_DISPLAY_DATE("Prism Cover Display Date"),
        PRISM_DOI("Prism Doi"),
        PII("Pii"),
        PUBMED_ID("Pubmed Id"),
        ORC_ID("Orc Id"),
        DC_CREATOR("Dc Creator"),
        AFFILIATIONS("Affiliations"),
        AUTHORS("Authors"),
        AUTHOR_COUNT("Author Count"),
        DC_DESCRIPTION("Dc Description"),
        AUTHOR_KEYWORDS("Author Keywords"),
        ARTICLE_NUMBER("Article Number"),
        SUBTYPE("Subtype"),
        SUBTYPE_DESCRIPTION("Subtype Description"),
        SOURCE_ID("Source Id"),
        FUNDING_AGENCY_ACRONYM("Funding Agency Acronym"),
        FUNDING_AGENCY_IDENTIFICATION("Funding Agency Identification"),
        FUNDING_AGENCY_NAME("Funding Agency Name"),
        MESSAGE("Message"),
        OPEN_ACCESS("Open Access"),
        OPEN_ACCESS_FLAG("Open Access Flag");
        private final String labelName;

        ScopusLabels(String labelName) {
            this.labelName = labelName;
        }

        public String getLabelName() {
            return labelName;
        }
    }


    public enum SciDirLabels {

        LOAD_DATE("Load Date"),
        LINKS("Links"),
        PRISM_URL("Prism Url"),
        DC_IDENTIFIER("Dc Identifier"),
        OPEN_ACCESS("Open Access"),
        OPEN_ACCESS_FLAG("Open Access Flag"),
        DC_TITLE("Dc Title"),
        PRISM_PUBLICATION_NAME("Prism Publication Name"),
        PRISM_ISBN("Prism Isbn"),
        PRISM_ISSN("Prism Issn"),
        PRISM_VOLUME("Prism Volume"),
        PRISM_ISSUE_IDENTIFIER("Prism Issue Identifier"),
        PRISM_ISSUE_NAME("Prism Issue Name"),
        PRISM_EDITION("Prism Edition"),
        PRISM_STARTING_PAGE("Prism Starting Page"),
        PRISM_ENDING_PAGE("Prism Ending Page"),
        PRISM_COVER_DATE("Prism Cover Date"),
        PRISM_COVER_DISPLAY_DATE("Prism Cover Display Date"),
        DC_CREATOR("Dc Creator"),
        AUTHORS("Authors"),
        PRISM_DOI("Prism Doi"),
        PII("Pii"),
        PUBTYPE("Pubtype"),
        PRISM_TEASER("Prism Teaser"),
        DC_DESCRIPTION("Dc Description"),
        AUTHOR_KEYWORDS("Author Keywords"),
        PRISM_AGGREGATION_TYPE("Prism Aggregation Type"),
        PRISM_COPYRIGHT("Prism Copyright"),
        SCOPUS_ID("Scopus Id"),
        EID("Eid"),
        SCOPUS_EID("Scopus Eid"),
        PUBMED_ID("Pubmed Id"),
        OPEN_ACCESS_ARTICLE("Open Access Article"),
        OPEN_ARCHIVE_ARTICLE("Open Archive Article"),
        OPEN_ACCESS_USER_LICENSE("Open Access User License"),
        AUTHORS_COLLABORATION("Authors Collaboration");

        private final String labelName;

        SciDirLabels(String labelName) {
            this.labelName = labelName;
        }

        public String getLabelName() {
            return labelName;
        }
    }


    public enum DblpLabels {

        DBLP_AUTHOR("Dblp Author"),

        KEY("Key"),
        MDATE("Mdate"),
        PUBLTYPE("Publtype"),
        REVIEW_ID("Review Id"),
        RATING("Rating"),
        CDATE("Cdate"),
        AUTHORS("Authors"),
        EDITORS("Editors"),
        TITLES("Titles"),
        BOOK_TITLES("Booktitles"),
        PAGES("Pages"),
        YEARS("Years"),
        ADDRESSES("Addresses"),
        JOURNALS("Journals"),
        VOLUMES("Volumes"),
        NUMBERS("Numbers"),
        MONTHS("Months"),
        URLS("Urls"),
        EES("Ees"),
        CD_ROMS("Cd roms"),
        CITES("Cites"),
        PUBLISHERS("Publishers"),
        NOTES("Notes"),
        CROSSREFS("Crossrefs"),
        ISBNS("Isbns"),
        SERIES("Series"),
        SCHOOLS("Schools"),
        CHAPTERS("Chapters"),
        PUBLNRS("Publnrs");

        private final String labelName;

        DblpLabels(String labelName) {
            this.labelName = labelName;
        }

        public String getLabelName() {
            return labelName;
        }
    }

    private final String labelName;

    MetadataLabelsHolder(String labelName) {
        this.labelName = labelName;
    }

    public String getLabelName() {
        return labelName;
    }
}
