package com.eresearch.repositorer.transformer.results.dblp.processor.common;


import com.eresearch.repositorer.dto.dblp.response.generated.*;
import com.eresearch.repositorer.dto.dblp.response.generated.Number;

import java.util.List;

public interface CommonDblpSource {

    String getKey();

    String getMdate();

    String getPubltype();

    String getReviewid();

    String getRating();

    String getCdate();

    List<Author> getAuthors();

    List<Editor> getEditors();

    List<Title> getTitles();

    List<Booktitle> getBooktitles();

    List<Pages> getPages();

    List<Year> getYears();

    List<Address> getAddresses();

    List<Journal> getJournals();

    List<Volume> getVolumes();

    List<Number> getNumbers();

    List<Month> getMonths();

    List<Url> getUrls();

    List<Ee> getEes();

    List<Cdrom> getCdroms();

    List<Cite> getCites();

    List<Publisher> getPublishers();

    List<Note> getNotes();

    List<Crossref> getCrossrefs();

    List<Isbn> getIsbns();

    List<Series> getSeries();

    List<School> getSchools();

    List<Chapter> getChapters();

    List<Publnr> getPublnrs();
}
