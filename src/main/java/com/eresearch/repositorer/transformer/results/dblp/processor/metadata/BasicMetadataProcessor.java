package com.eresearch.repositorer.transformer.results.dblp.processor.metadata;

import com.eresearch.repositorer.domain.record.metadata.MetadataLabelsHolder;
import com.eresearch.repositorer.domain.record.metadata.Source;
import com.eresearch.repositorer.dto.dblp.response.DblpAuthor;
import com.eresearch.repositorer.dto.dblp.response.generated.*;
import com.eresearch.repositorer.dto.dblp.response.generated.Number;
import com.eresearch.repositorer.transformer.results.dblp.processor.common.CommonDblpSource;
import com.eresearch.repositorer.transformer.results.dblp.processor.common.ObjectAcceptor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BasicMetadataProcessor {

    private final ObjectMapper objectMapper;

    @Autowired
    public BasicMetadataProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Map<String, String> metadataPopulation(Object source, DblpAuthor dblpAuthor) throws JsonProcessingException {

        final CommonDblpSource record = ObjectAcceptor.isAcceptedObject(source);

        Map<String, String> metadata = new LinkedHashMap<>();

        metadata.put(MetadataLabelsHolder.SOURCE.getLabelName(),
                Source.DBLP.getSourceName());

        String dblpAuthorAsString = objectMapper.writeValueAsString(dblpAuthor);
        metadata.put(MetadataLabelsHolder.DblpLabels.DBLP_AUTHOR.getLabelName(),
                dblpAuthorAsString);

        String key = record.getKey();
        metadata.put(MetadataLabelsHolder.DblpLabels.KEY.getLabelName(),
                key);

        String mdate = record.getMdate();
        metadata.put(MetadataLabelsHolder.DblpLabels.MDATE.getLabelName(),
                mdate);

        String publtype = record.getPubltype();
        metadata.put(MetadataLabelsHolder.DblpLabels.PUBLTYPE.getLabelName(),
                publtype);

        //NO reviewId.

        //NO rating.

        String cdate = record.getCdate();
        metadata.put(MetadataLabelsHolder.DblpLabels.CDATE.getLabelName(),
                cdate);

        List<Author> authors = record.getAuthors();
        if (authors != null && !authors.isEmpty()) {

            List<String> authorsFullnames = authors
                    .stream()
                    .map(com.eresearch.repositorer.dto.dblp.response.generated.Author::getValue)
                    .collect(Collectors.toList());

            String authorsFullnamesAsString = objectMapper.writeValueAsString(authorsFullnames);

            metadata.put(MetadataLabelsHolder.DblpLabels.AUTHORS.getLabelName(),
                    authorsFullnamesAsString);

        } else {
            metadata.put(MetadataLabelsHolder.DblpLabels.AUTHORS.getLabelName(),
                    null);
        }

        List<Editor> editors = record.getEditors();
        if (editors != null && !editors.isEmpty()) {

            List<String> editorsFullnames = editors
                    .stream()
                    .map(Editor::getValue)
                    .collect(Collectors.toList());

            String editorsFullnamesAsString = objectMapper.writeValueAsString(editorsFullnames);

            metadata.put(MetadataLabelsHolder.DblpLabels.EDITORS.getLabelName(),
                    editorsFullnamesAsString);

        } else {
            metadata.put(MetadataLabelsHolder.DblpLabels.EDITORS.getLabelName(),
                    null);
        }

        List<Title> titles = record.getTitles();
        if (titles != null && !titles.isEmpty()) {


            List<String> titlesValues = titles.stream().map(Title::getValue).collect(Collectors.toList());

            String titlesValuesAsString = objectMapper.writeValueAsString(titlesValues);

            metadata.put(MetadataLabelsHolder.DblpLabels.TITLES.getLabelName(),
                    titlesValuesAsString);

        } else {
            metadata.put(MetadataLabelsHolder.DblpLabels.TITLES.getLabelName(),
                    null);
        }

        List<Booktitle> booktitles = record.getBooktitles();
        if (booktitles != null && !booktitles.isEmpty()) {

            List<String> booktitlesValues = booktitles.stream().map(Booktitle::getValue).collect(Collectors.toList());

            String booktitlesValuesAsString = objectMapper.writeValueAsString(booktitlesValues);

            metadata.put(MetadataLabelsHolder.DblpLabels.BOOK_TITLES.getLabelName(),
                    booktitlesValuesAsString);

        } else {
            metadata.put(MetadataLabelsHolder.DblpLabels.BOOK_TITLES.getLabelName(),
                    null);
        }

        List<Pages> pages = record.getPages();
        if (pages != null && !pages.isEmpty()) {

            List<String> pagesValues = pages.stream().map(Pages::getValue).collect(Collectors.toList());

            String pagesValuesAsString = objectMapper.writeValueAsString(pagesValues);

            metadata.put(MetadataLabelsHolder.DblpLabels.PAGES.getLabelName(),
                    pagesValuesAsString);

        } else {
            metadata.put(MetadataLabelsHolder.DblpLabels.PAGES.getLabelName(),
                    null);
        }

        List<Year> years = record.getYears();
        if (years != null && !years.isEmpty()) {

            List<String> yearsValues = years.stream().map(Year::getValue).collect(Collectors.toList());

            String yearsValuesAsString = objectMapper.writeValueAsString(yearsValues);

            metadata.put(MetadataLabelsHolder.DblpLabels.YEARS.getLabelName(),
                    yearsValuesAsString);

        } else {
            metadata.put(MetadataLabelsHolder.DblpLabels.YEARS.getLabelName(),
                    null);
        }

        List<Address> addresses = record.getAddresses();
        if (addresses != null && !addresses.isEmpty()) {

            List<String> addressesValues = addresses.stream().map(Address::getValue).collect(Collectors.toList());

            String addressesValuesAsString = objectMapper.writeValueAsString(addressesValues);

            metadata.put(MetadataLabelsHolder.DblpLabels.ADDRESSES.getLabelName(),
                    addressesValuesAsString);

        } else {
            metadata.put(MetadataLabelsHolder.DblpLabels.ADDRESSES.getLabelName(),
                    null);
        }

        List<Journal> journals = record.getJournals();
        if (journals != null && !journals.isEmpty()) {

            List<String> journalsValues = journals.stream().map(Journal::getValue).collect(Collectors.toList());

            String journalsValuesAsString = objectMapper.writeValueAsString(journalsValues);

            metadata.put(MetadataLabelsHolder.DblpLabels.JOURNALS.getLabelName(),
                    journalsValuesAsString);

        } else {
            metadata.put(MetadataLabelsHolder.DblpLabels.JOURNALS.getLabelName(),
                    null);
        }

        List<Volume> volumes = record.getVolumes();
        if (volumes != null && !volumes.isEmpty()) {

            List<String> volumesValues = volumes.stream().map(Volume::getValue).collect(Collectors.toList());

            String volumesValuesAsString = objectMapper.writeValueAsString(volumesValues);

            metadata.put(MetadataLabelsHolder.DblpLabels.VOLUMES.getLabelName(),
                    volumesValuesAsString);

        } else {
            metadata.put(MetadataLabelsHolder.DblpLabels.VOLUMES.getLabelName(),
                    null);
        }

        List<Number> numbers = record.getNumbers();
        if (numbers != null && !numbers.isEmpty()) {

            List<String> numbersValues = numbers.stream().map(Number::getValue).collect(Collectors.toList());

            String numbersValuesAsString = objectMapper.writeValueAsString(numbersValues);

            metadata.put(MetadataLabelsHolder.DblpLabels.NUMBERS.getLabelName(),
                    numbersValuesAsString);

        } else {
            metadata.put(MetadataLabelsHolder.DblpLabels.NUMBERS.getLabelName(),
                    null);
        }

        List<Month> months = record.getMonths();
        if (months != null && !months.isEmpty()) {

            List<String> monthsValues = months.stream().map(Month::getValue).collect(Collectors.toList());

            String monthsValuesAsString = objectMapper.writeValueAsString(monthsValues);

            metadata.put(MetadataLabelsHolder.DblpLabels.MONTHS.getLabelName(),
                    monthsValuesAsString);

        } else {
            metadata.put(MetadataLabelsHolder.DblpLabels.MONTHS.getLabelName(),
                    null);
        }

        List<Url> urls = record.getUrls();
        if (urls != null && !urls.isEmpty()) {

            List<String> urlsValues = urls.stream().map(Url::getValue).collect(Collectors.toList());

            String urlsValuesAsString = objectMapper.writeValueAsString(urlsValues);

            metadata.put(MetadataLabelsHolder.DblpLabels.URLS.getLabelName(),
                    urlsValuesAsString);

        } else {
            metadata.put(MetadataLabelsHolder.DblpLabels.URLS.getLabelName(),
                    null);
        }

        List<Ee> ees = record.getEes();
        if (ees != null && !ees.isEmpty()) {

            List<String> eesValues = ees.stream().map(Ee::getValue).collect(Collectors.toList());

            String eesValuesAsString = objectMapper.writeValueAsString(eesValues);

            metadata.put(MetadataLabelsHolder.DblpLabels.EES.getLabelName(),
                    eesValuesAsString);

        } else {
            metadata.put(MetadataLabelsHolder.DblpLabels.EES.getLabelName(),
                    null);
        }

        List<Cdrom> cdroms = record.getCdroms();
        if (cdroms != null && !cdroms.isEmpty()) {

            List<String> cdromsValues = cdroms.stream().map(Cdrom::getValue).collect(Collectors.toList());

            String cdromsValuesAsString = objectMapper.writeValueAsString(cdromsValues);

            metadata.put(MetadataLabelsHolder.DblpLabels.CD_ROMS.getLabelName(),
                    cdromsValuesAsString);

        } else {
            metadata.put(MetadataLabelsHolder.DblpLabels.CD_ROMS.getLabelName(),
                    null);
        }

        List<Cite> cites = record.getCites();
        if (cites != null && !cites.isEmpty()) {

            List<String> citesValues = cites.stream().map(Cite::getValue).collect(Collectors.toList());

            String citesValuesAsString = objectMapper.writeValueAsString(citesValues);

            metadata.put(MetadataLabelsHolder.DblpLabels.CITES.getLabelName(),
                    citesValuesAsString);

        } else {
            metadata.put(MetadataLabelsHolder.DblpLabels.CITES.getLabelName(),
                    null);
        }

        List<Publisher> publishers = record.getPublishers();
        if (publishers != null && !publishers.isEmpty()) {

            List<String> publishersValues = publishers.stream().map(Publisher::getValue).collect(Collectors.toList());

            String publishersValuesAsString = objectMapper.writeValueAsString(publishersValues);

            metadata.put(MetadataLabelsHolder.DblpLabels.PUBLISHERS.getLabelName(),
                    publishersValuesAsString);

        } else {
            metadata.put(MetadataLabelsHolder.DblpLabels.PUBLISHERS.getLabelName(),
                    null);
        }

        List<Note> notes = record.getNotes();
        if (notes != null && !notes.isEmpty()) {

            List<String> notesValues = notes.stream().map(Note::getValue).collect(Collectors.toList());

            String notesValuesAsString = objectMapper.writeValueAsString(notesValues);

            metadata.put(MetadataLabelsHolder.DblpLabels.NOTES.getLabelName(),
                    notesValuesAsString);

        } else {
            metadata.put(MetadataLabelsHolder.DblpLabels.NOTES.getLabelName(),
                    null);
        }

        List<Crossref> crossrefs = record.getCrossrefs();
        if (crossrefs != null && !crossrefs.isEmpty()) {

            List<String> crossrefsValues = crossrefs.stream().map(Crossref::getValue).collect(Collectors.toList());

            String crossrefsValuesAsString = objectMapper.writeValueAsString(crossrefsValues);

            metadata.put(MetadataLabelsHolder.DblpLabels.CROSSREFS.getLabelName(),
                    crossrefsValuesAsString);

        } else {
            metadata.put(MetadataLabelsHolder.DblpLabels.CROSSREFS.getLabelName(),
                    null);
        }

        List<Isbn> isbns = record.getIsbns();
        if (isbns != null && !isbns.isEmpty()) {

            List<String> isbnsValues = isbns.stream().map(Isbn::getValue).collect(Collectors.toList());

            String isbnsValuesAsString = objectMapper.writeValueAsString(isbnsValues);

            metadata.put(MetadataLabelsHolder.DblpLabels.ISBNS.getLabelName(),
                    isbnsValuesAsString);

        } else {
            metadata.put(MetadataLabelsHolder.DblpLabels.ISBNS.getLabelName(),
                    null);
        }

        List<Series> series = record.getSeries();
        if (series != null && !series.isEmpty()) {

            List<String> seriesValues = series.stream().map(Series::getValue).collect(Collectors.toList());

            String seriesValuesAsString = objectMapper.writeValueAsString(seriesValues);

            metadata.put(MetadataLabelsHolder.DblpLabels.SERIES.getLabelName(),
                    seriesValuesAsString);

        } else {

            metadata.put(MetadataLabelsHolder.DblpLabels.SERIES.getLabelName(),
                    null);

        }

        List<School> schools = record.getSchools();
        if (schools != null && !schools.isEmpty()) {

            List<String> schoolsValues = schools.stream().map(School::getValue).collect(Collectors.toList());

            String schoolsValuesAsString = objectMapper.writeValueAsString(schoolsValues);

            metadata.put(MetadataLabelsHolder.DblpLabels.SCHOOLS.getLabelName(),
                    schoolsValuesAsString);

        } else {
            metadata.put(MetadataLabelsHolder.DblpLabels.SCHOOLS.getLabelName(),
                    null);
        }

        List<Chapter> chapters = record.getChapters();
        if (chapters != null && !chapters.isEmpty()) {

            List<String> chaptersValues = chapters.stream().map(Chapter::getValue).collect(Collectors.toList());

            String chaptersValuesAsString = objectMapper.writeValueAsString(chaptersValues);

            metadata.put(MetadataLabelsHolder.DblpLabels.CHAPTERS.getLabelName(),
                    chaptersValuesAsString);

        } else {
            metadata.put(MetadataLabelsHolder.DblpLabels.CHAPTERS.getLabelName(),
                    null);
        }

        List<Publnr> publnrs = record.getPublnrs();
        if (publnrs != null && !publnrs.isEmpty()) {

            List<String> publnrsValues = publnrs.stream().map(Publnr::getValue).collect(Collectors.toList());

            String publnrsValuesAsString = objectMapper.writeValueAsString(publnrsValues);

            metadata.put(MetadataLabelsHolder.DblpLabels.PUBLNRS.getLabelName(),
                    publnrsValuesAsString);

        } else {
            metadata.put(MetadataLabelsHolder.DblpLabels.PUBLNRS.getLabelName(),
                    null);
        }
        return metadata;
    }

}
