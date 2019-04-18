package com.eresearch.repositorer.repository;


import com.eresearch.repositorer.domain.error.ErrorReport;
import com.eresearch.repositorer.dto.repositorer.request.ErrorReportSearchDto;
import com.eresearch.repositorer.dto.repositorer.response.RetrievedErrorReportDto;
import com.eresearch.repositorer.exception.business.RepositorerBusinessException;
import com.eresearch.repositorer.exception.error.RepositorerError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Pattern;

@Log4j
@Repository
public class ErrorReportRepository implements EresearchRepositorerRepository<ErrorReport, RetrievedErrorReportDto, ErrorReportSearchDto> {

    private static final boolean APPLY_DISTINCTION_ON_SEARCH_RESULTS = false;

    private static final String FILENAME_PREFIX = "ERROR_REPORT";

    private static final String RECORD_TYPE = "ERROR_REPORT";

    private static final String REGEX_FOR_ALL = ".*";

    private static final String CONTENT_TYPE = "application/octet-stream";

    private static final String DATE_TIME_SPLIT_TOKEN = "T";

    private final Pattern findAllRecordsPattern;
    private final GridFsTemplate gridFsTemplate;
    private final Clock clock;
    private final ObjectMapper objectMapper;

    @Autowired
    public ErrorReportRepository(GridFsTemplate gridFsTemplate, Clock clock, ObjectMapper objectMapper) {
        this.gridFsTemplate = gridFsTemplate;
        this.clock = clock;
        this.objectMapper = objectMapper;

        findAllRecordsPattern = Pattern.compile(REGEX_FOR_ALL);
    }

    @Override
    public boolean deleteAll() {
        // first perform all necessary validations....
        List<GridFSDBFile> gridFSDBFiles = gridFsTemplate.find(Query.query(Criteria.where("filename").regex(FILENAME_PREFIX + findAllRecordsPattern)));
        if (gridFSDBFiles.size() == 0) {
            throw new RepositorerBusinessException(RepositorerError.REPOSITORY_IS_EMPTY,
                    RepositorerError.REPOSITORY_IS_EMPTY.getMessage());
        }

        // then perform delete operation...
        gridFsTemplate.delete(Query.query(Criteria.where("filename").regex(FILENAME_PREFIX + findAllRecordsPattern)));

        // and then perform all necessary validations again...
        return findAll(false).size() == 0;
    }

    @Override
    public boolean delete(String filename) {

        // first perform all necessary validations....
        List<GridFSDBFile> gridFSDBFiles = gridFsTemplate.find(Query.query(Criteria.where("filename").is(filename)));

        if (gridFSDBFiles.size() > 1) {
            throw new RepositorerBusinessException(RepositorerError.NO_UNIQUE_RESULTS_EXIST_BASED_ON_PROVIDED_DISCRIMINATOR,
                    RepositorerError.NO_UNIQUE_RESULTS_EXIST_BASED_ON_PROVIDED_DISCRIMINATOR.getMessage());
        }

        if (gridFSDBFiles.size() == 0) {
            throw new RepositorerBusinessException(RepositorerError.RECORD_DOES_NOT_EXIST,
                    RepositorerError.RECORD_DOES_NOT_EXIST.getMessage());
        }

        // then perform delete operation...
        gridFsTemplate.delete(Query.query(Criteria.where("filename").is(filename)));

        // and then perform all necessary validations again...
        gridFSDBFiles = gridFsTemplate.find(Query.query(Criteria.where("filename").is(filename)));
        return gridFSDBFiles.size() == 0;
    }

    @Override
    public Collection<RetrievedErrorReportDto> find(String filename, boolean fullFetch) {
        throw new UnsupportedOperationException("No need implementation for the moment.");
    }

    @Override
    public void store(ErrorReport errorReport) {

        //first serialize record...
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorReport);

            //then transform the serialized record into an input stream...
            try (InputStream is = new ByteArrayInputStream(bytes)) {

                //finally store the info...
                String fileName = FILENAME_PREFIX + errorReport.getCreatedAt().toString();
                DBObject metaData = createMetadata(errorReport);
                gridFsTemplate.store(is, fileName, CONTENT_TYPE, metaData);
            }

        } catch (Exception error) {
            log.error("ErrorReportRepository#store --- error occurred.", error);
            throw new RepositorerBusinessException(RepositorerError.COULD_NOT_SERIALIZE_OBJECT,
                    RepositorerError.COULD_NOT_SERIALIZE_OBJECT.getMessage(),
                    error,
                    this.getClass().getName());
        }
    }

    @Override
    public Collection<RetrievedErrorReportDto> findAll(boolean fullFetch) {
        final List<RetrievedErrorReportDto> retrievedDiscardedMessageDtos = new ArrayList<>();

        List<GridFSDBFile> gridFSDBFiles = gridFsTemplate.find(Query.query(Criteria.where("filename").regex(FILENAME_PREFIX + findAllRecordsPattern)));

        populateRetrievedRecordsWithResults(retrievedDiscardedMessageDtos, gridFSDBFiles, fullFetch);

        return retrievedDiscardedMessageDtos;
    }

    @Override
    public Collection<RetrievedErrorReportDto> find(boolean fullFetch, ErrorReportSearchDto errorReportSearchDto) {
        //------------- do main search --------------
        final Collection<RetrievedErrorReportDto> retrievedErrorReportDtos = APPLY_DISTINCTION_ON_SEARCH_RESULTS ? new HashSet<>() : new ArrayList<>();

        String filenameToSearchFor = createFilenameToSearchFor(errorReportSearchDto);

        final List<GridFSDBFile> retrievedGridFSDBFilesFromMainSearch
                = gridFsTemplate.find(Query.query(Criteria.where("filename").regex(filenameToSearchFor)));

        populateRetrievedRecordsWithResults(retrievedErrorReportDtos, retrievedGridFSDBFilesFromMainSearch, fullFetch);

        //--------- and then do additional search (if selected) based on provided name variants ------------
        List<GridFSDBFile> retrievedGridFSDBFilesFromNameVariantsSearch = gridFsTemplate.find(
                Query.query(
                        Criteria.where("metadata.recordCreatedDate")
                                .regex(REGEX_FOR_ALL
                                        + errorReportSearchDto.getDate().toString()
                                        + REGEX_FOR_ALL)
                                .and("metadata.recordCreatedTime")
                                .regex(REGEX_FOR_ALL
                                        + normalizeTime(errorReportSearchDto)
                                        + REGEX_FOR_ALL)
                                .and("metadata.recordType")
                                .regex(RECORD_TYPE)
                )
        );

        populateRetrievedRecordsWithResults(retrievedErrorReportDtos, retrievedGridFSDBFilesFromNameVariantsSearch, fullFetch);

        return retrievedErrorReportDtos;
    }

    private String createFilenameToSearchFor(ErrorReportSearchDto errorReportSearchDto) {
        return FILENAME_PREFIX
                + errorReportSearchDto.getDate()
                + REGEX_FOR_ALL
                + DATE_TIME_SPLIT_TOKEN
                + normalizeTime(errorReportSearchDto)
                + REGEX_FOR_ALL;
    }

    private String normalizeTime(ErrorReportSearchDto errorReportSearchDto) {
        return Optional.ofNullable(errorReportSearchDto.getTime()).map(LocalTime::toString).orElse("");
    }

    private void populateRetrievedRecordsWithResults(Collection<RetrievedErrorReportDto> retrievedErrorReportDtos, List<GridFSDBFile> gridFSDBFiles, boolean fullFetch) {

        for (GridFSDBFile gridFSDBFile : gridFSDBFiles) {

            String filename = gridFSDBFile.getFilename();

            RetrievedErrorReportDto retrievedErrorReportDto = new RetrievedErrorReportDto();
            retrievedErrorReportDto.setFilename(filename);

            if (fullFetch) {
                ErrorReport errorReport = deserializeStoredRecord(gridFSDBFile.getInputStream());
                retrievedErrorReportDto.setErrorReport(errorReport);
            }

            retrievedErrorReportDtos.add(retrievedErrorReportDto);
        }
    }

    private ErrorReport deserializeStoredRecord(InputStream inputStream) {

        try {

            byte[] bytes = ByteStreams.toByteArray(inputStream);
            return objectMapper.readValue(bytes, ErrorReport.class);

        } catch (Exception error) {

            log.error("ErrorReportRepository#deserializeStoredRecord --- error occurred.", error);
            throw new RepositorerBusinessException(RepositorerError.COULD_NOT_DESERIALIZE_OBJECT,
                    RepositorerError.COULD_NOT_DESERIALIZE_OBJECT.getMessage(),
                    error,
                    this.getClass().getName());
        }
    }

    private DBObject createMetadata(ErrorReport errorReport) {
        DBObject metaData = new BasicDBObject();

        Instant createdAt = errorReport.getCreatedAt();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(createdAt, clock.getZone());

        metaData.put("recordCreatedDate", localDateTime.toLocalDate().toString());
        metaData.put("recordCreatedTime", localDateTime.toLocalTime().toString());
        metaData.put("recordType", RECORD_TYPE);

        return metaData;
    }

}
