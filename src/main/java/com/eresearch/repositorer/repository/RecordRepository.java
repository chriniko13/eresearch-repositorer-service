package com.eresearch.repositorer.repository;

import com.eresearch.repositorer.domain.record.Author;
import com.eresearch.repositorer.domain.record.Record;
import com.eresearch.repositorer.dto.repositorer.response.RetrievedRecordDto;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Log4j
@Repository
public class RecordRepository implements EresearchRepositorerRepository<Record, RetrievedRecordDto, Author> {

    private static final String FILENAME_PREFIX = "RECORD";

    private static final String RECORD_TYPE = "RECORD";

    private static final boolean APPLY_DISTINCTION_ON_SEARCH_RESULTS = true;

    private static final String CONTENT_TYPE = "application/octet-stream";

    private static final String NO_VALUE = "NoValue";

    private static final String REGEX_FOR_ALL = ".*";

    private final GridFsTemplate gridFsTemplate;
    private final Clock clock;
    private final Pattern findAllRecordsPattern;
    private final ObjectMapper objectMapper;

    @Autowired
    public RecordRepository(GridFsTemplate gridFsTemplate, Clock clock, ObjectMapper objectMapper) {
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
    public Collection<RetrievedRecordDto> find(String filename, boolean fullFetch) {

        final Collection<RetrievedRecordDto> retrievedRecordDtos = new ArrayList<>();

        List<GridFSDBFile> gridFSDBFiles = gridFsTemplate.find(Query.query(Criteria.where("filename").is(filename)));

        populateRetrievedRecordsWithResults(retrievedRecordDtos, gridFSDBFiles, fullFetch);

        return retrievedRecordDtos;
    }

    @Override
    public Collection<RetrievedRecordDto> find(boolean fullFetch, Author mainAuthorName) {

        //------------- do main search --------------
        final Collection<RetrievedRecordDto> retrievedRecordDtos = APPLY_DISTINCTION_ON_SEARCH_RESULTS ? new HashSet<>() : new ArrayList<>();

        String filenameToSearchFor = createFilenameToSearchFor(mainAuthorName);

        final List<GridFSDBFile> retrievedGridFSDBFilesFromMainSearch
                = gridFsTemplate.find(Query.query(Criteria.where("filename").regex(filenameToSearchFor)));

        populateRetrievedRecordsWithResults(retrievedRecordDtos, retrievedGridFSDBFilesFromMainSearch, fullFetch);

        //--------- and then do additional search based on splitted metadata info ------------
        List<GridFSDBFile> retrievedGridFSDBFilesFromNameVariantsSearch = gridFsTemplate.find(
                Query.query(
                        Criteria.where("metadata.authorRecordName.firstname")
                                .regex(REGEX_FOR_ALL + mainAuthorName.getFirstname() + REGEX_FOR_ALL)
                                .and("metadata.authorRecordName.initials")
                                .regex(REGEX_FOR_ALL + mainAuthorName.getInitials() + REGEX_FOR_ALL)
                                .and("metadata.authorRecordName.lastname")
                                .regex(REGEX_FOR_ALL + mainAuthorName.getSurname() + REGEX_FOR_ALL)
                                .and("metadata.recordType")
                                .regex(RECORD_TYPE)
                )
        );

        populateRetrievedRecordsWithResults(retrievedRecordDtos, retrievedGridFSDBFilesFromNameVariantsSearch, fullFetch);

        return retrievedRecordDtos;
    }

    @Override
    public Collection<RetrievedRecordDto> findAll(boolean fullFetch) {
        final List<RetrievedRecordDto> retrievedRecordDtos = new ArrayList<>();

        List<GridFSDBFile> gridFSDBFiles = gridFsTemplate.find(Query.query(Criteria.where("filename").regex(FILENAME_PREFIX + findAllRecordsPattern)));

        populateRetrievedRecordsWithResults(retrievedRecordDtos, gridFSDBFiles, fullFetch);

        return retrievedRecordDtos;
    }

    @Override
    public void store(Record record) {

        //first serialize record...
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(record);

            //then transform the serialized record into an input stream...
            try (InputStream is = new ByteArrayInputStream(bytes)) {

                //finally store the info...
                String fileName = createFilename(record) + "#" + getStringifiedDate(record);
                DBObject metaData = createMetadata(record);
                gridFsTemplate.store(is, fileName, CONTENT_TYPE, metaData);
            }

        } catch (Exception error) {
            log.error("RecordRepository#store --- error occurred.", error);
            throw new RepositorerBusinessException(RepositorerError.COULD_NOT_SERIALIZE_OBJECT,
                    RepositorerError.COULD_NOT_SERIALIZE_OBJECT.getMessage(),
                    error,
                    this.getClass().getName());
        }
    }

    private String createFilename(Record record) {
        return FILENAME_PREFIX
                + normalizeInfo(record.getFirstname())
                + "_"
                + normalizeInfo(record.getInitials())
                + "_"
                + normalizeInfo(record.getLastname());
    }

    private DBObject createMetadata(Record record) {
        DBObject metaData = new BasicDBObject();

        DBObject authorRecordName = new BasicDBObject();
        authorRecordName.put("firstname", record.getFirstname());
        authorRecordName.put("initials", record.getInitials());
        authorRecordName.put("lastname", record.getLastname());

        metaData.put("authorRecordName", authorRecordName);
        metaData.put("recordCreatedAt", getStringifiedDate(record));
        metaData.put("recordType", RECORD_TYPE);

        return metaData;
    }

    private String getStringifiedDate(Record record) {
        return LocalDateTime.ofInstant(record.getCreatedAt(), clock.getZone()).toString();
    }

    private String normalizeInfo(String info) {
        return Optional.ofNullable(info).filter(i -> !i.isEmpty()).orElse(NO_VALUE);
    }

    private String createFilenameToSearchFor(Author author) {
        return FILENAME_PREFIX
                + normalizeInfo(author.getFirstname()) + REGEX_FOR_ALL
                + "_"
                + normalizeInfo(author.getInitials()) + REGEX_FOR_ALL
                + "_"
                + normalizeInfo(author.getSurname()) + REGEX_FOR_ALL;
    }

    private void populateRetrievedRecordsWithResults(Collection<RetrievedRecordDto> retrievedRecordDtos, List<GridFSDBFile> gridFSDBFiles, boolean fullFetch) {

        for (GridFSDBFile gridFSDBFile : gridFSDBFiles) {

            String filename = gridFSDBFile.getFilename();

            RetrievedRecordDto retrievedRecordDto = new RetrievedRecordDto();
            retrievedRecordDto.setFilename(filename);

            if (fullFetch) {
                Record record = deserializeStoredRecord(gridFSDBFile.getInputStream());
                retrievedRecordDto.setRecord(record);
            }

            retrievedRecordDtos.add(retrievedRecordDto);
        }
    }

    private Record deserializeStoredRecord(InputStream inputStream) {
        try {

            byte[] bytes = ByteStreams.toByteArray(inputStream);
            return objectMapper.readValue(bytes, Record.class);

        } catch (Exception error) {

            log.error("RecordRepository#deserializeStoredRecord --- error occurred.", error);
            throw new RepositorerBusinessException(RepositorerError.COULD_NOT_DESERIALIZE_OBJECT,
                    RepositorerError.COULD_NOT_DESERIALIZE_OBJECT.getMessage(),
                    error,
                    this.getClass().getName());
        }
    }
}
