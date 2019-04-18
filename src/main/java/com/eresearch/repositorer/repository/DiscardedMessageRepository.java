package com.eresearch.repositorer.repository;

import com.eresearch.repositorer.domain.discard.DiscardedMessage;
import com.eresearch.repositorer.dto.repositorer.request.DiscardedMessageSearchDto;
import com.eresearch.repositorer.dto.repositorer.response.RetrievedDiscardedMessageDto;
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
public class DiscardedMessageRepository implements EresearchRepositorerRepository<DiscardedMessage, RetrievedDiscardedMessageDto, DiscardedMessageSearchDto> {

    private static final String FILENAME_PREFIX = "DISCARDED_MESSAGE";

    private static final String RECORD_TYPE = "DISCARDED_MESSAGE";

    private static final boolean APPLY_DISTINCTION_ON_SEARCH_RESULTS = false;

    private static final String REGEX_FOR_ALL = ".*";

    private static final String CONTENT_TYPE = "application/octet-stream";

    private static final String DATE_TIME_SPLIT_TOKEN = "T";

    private final Pattern findAllRecordsPattern;

    private final GridFsTemplate gridFsTemplate;
    private final Clock clock;
    private final ObjectMapper objectMapper;

    @Autowired
    public DiscardedMessageRepository(GridFsTemplate gridFsTemplate, Clock clock, ObjectMapper objectMapper) {
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
    public Collection<RetrievedDiscardedMessageDto> find(String filename, boolean fullFetch) {
        throw new UnsupportedOperationException("No need implementation for the moment.");
    }

    @Override
    public void store(DiscardedMessage discardedMessage) {

        //first serialize record...
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(discardedMessage);

            //then transform the serialized record into an input stream...
            try (InputStream is = new ByteArrayInputStream(bytes)) {

                //finally store the info...
                String fileName = FILENAME_PREFIX + discardedMessage.getCreatedAt().toString();
                DBObject metaData = createMetadata(discardedMessage);
                gridFsTemplate.store(is, fileName, CONTENT_TYPE, metaData);
            }

        } catch (Exception error) {
            log.error("DiscardedMessageRepository#store --- error occurred.", error);
            throw new RepositorerBusinessException(RepositorerError.COULD_NOT_SERIALIZE_OBJECT,
                    RepositorerError.COULD_NOT_SERIALIZE_OBJECT.getMessage(),
                    error,
                    this.getClass().getName());
        }
    }

    @Override
    public Collection<RetrievedDiscardedMessageDto> findAll(boolean fullFetch) {
        final List<RetrievedDiscardedMessageDto> retrievedDiscardedMessageDtos = new ArrayList<>();

        List<GridFSDBFile> gridFSDBFiles = gridFsTemplate.find(Query.query(Criteria.where("filename").regex(FILENAME_PREFIX + findAllRecordsPattern)));

        populateRetrievedRecordsWithResults(retrievedDiscardedMessageDtos, gridFSDBFiles, fullFetch);

        return retrievedDiscardedMessageDtos;
    }

    @Override
    public Collection<RetrievedDiscardedMessageDto> find(boolean fullFetch, DiscardedMessageSearchDto discardedMessageSearchDto) {
        //------------- do main search --------------
        final Collection<RetrievedDiscardedMessageDto> retrievedDiscardedMessageDtos = APPLY_DISTINCTION_ON_SEARCH_RESULTS ? new HashSet<>() : new ArrayList<>();

        String filenameToSearchFor = createFilenameToSearchFor(discardedMessageSearchDto);

        final List<GridFSDBFile> retrievedGridFSDBFilesFromMainSearch
                = gridFsTemplate.find(Query.query(Criteria.where("filename").regex(filenameToSearchFor)));

        populateRetrievedRecordsWithResults(retrievedDiscardedMessageDtos, retrievedGridFSDBFilesFromMainSearch, fullFetch);

        //--------- and then do additional search (if selected) based on provided name variants ------------
        List<GridFSDBFile> retrievedGridFSDBFilesFromNameVariantsSearch = gridFsTemplate.find(
                Query.query(
                        Criteria.where("metadata.recordCreatedDate")
                                .regex(REGEX_FOR_ALL
                                        + discardedMessageSearchDto.getDate().toString()
                                        + REGEX_FOR_ALL)
                                .and("metadata.recordCreatedTime")
                                .regex(REGEX_FOR_ALL
                                        + normalizeTime(discardedMessageSearchDto)
                                        + REGEX_FOR_ALL)
                                .and("metadata.recordType")
                                .regex(RECORD_TYPE)
                )
        );

        populateRetrievedRecordsWithResults(retrievedDiscardedMessageDtos, retrievedGridFSDBFilesFromNameVariantsSearch, fullFetch);

        return retrievedDiscardedMessageDtos;
    }

    private String createFilenameToSearchFor(DiscardedMessageSearchDto discardedMessageSearchDto) {
        return FILENAME_PREFIX
                + discardedMessageSearchDto.getDate()
                + REGEX_FOR_ALL
                + DATE_TIME_SPLIT_TOKEN
                + normalizeTime(discardedMessageSearchDto)
                + REGEX_FOR_ALL;
    }

    private String normalizeTime(DiscardedMessageSearchDto discardedMessageSearchDto) {
        return Optional.ofNullable(discardedMessageSearchDto.getTime()).map(LocalTime::toString).orElse("");
    }

    private void populateRetrievedRecordsWithResults(Collection<RetrievedDiscardedMessageDto> retrievedDiscardedMessageDtos, List<GridFSDBFile> gridFSDBFiles, boolean fullFetch) {

        for (GridFSDBFile gridFSDBFile : gridFSDBFiles) {

            String filename = gridFSDBFile.getFilename();

            RetrievedDiscardedMessageDto retrievedDiscardedMessageDto = new RetrievedDiscardedMessageDto();
            retrievedDiscardedMessageDto.setFilename(filename);

            if (fullFetch) {
                DiscardedMessage discardedMessage = deserializeStoredRecord(gridFSDBFile.getInputStream());
                retrievedDiscardedMessageDto.setDiscardedMessage(discardedMessage);
            }

            retrievedDiscardedMessageDtos.add(retrievedDiscardedMessageDto);
        }
    }

    private DiscardedMessage deserializeStoredRecord(InputStream inputStream) {

        try {

            byte[] bytes = ByteStreams.toByteArray(inputStream);
            return objectMapper.readValue(bytes, DiscardedMessage.class);

        } catch (Exception error) {

            log.error("DiscardedMessageRepository#deserializeStoredRecord --- error occurred.", error);
            throw new RepositorerBusinessException(RepositorerError.COULD_NOT_DESERIALIZE_OBJECT,
                    RepositorerError.COULD_NOT_DESERIALIZE_OBJECT.getMessage(),
                    error,
                    this.getClass().getName());
        }
    }

    private DBObject createMetadata(DiscardedMessage discardedMessage) {
        DBObject metaData = new BasicDBObject();

        Instant createdAt = discardedMessage.getCreatedAt();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(createdAt, clock.getZone());

        metaData.put("recordCreatedDate", localDateTime.toLocalDate().toString());
        metaData.put("recordCreatedTime", localDateTime.toLocalTime().toString());
        metaData.put("recordType", RECORD_TYPE);

        return metaData;
    }

}
