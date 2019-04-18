package com.eresearch.repositorer.validator;

import com.eresearch.repositorer.dto.repositorer.request.RepositorerFindDto;
import com.eresearch.repositorer.dto.repositorer.request.RepositorerFindDtos;
import com.eresearch.repositorer.exception.data.RepositorerValidationException;
import com.eresearch.repositorer.exception.error.RepositorerError;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.stream.Stream;

/*
    Note: Only initials could be empty or null.
 */
@Component
@Log4j
public class RepositorerFindDtosValidator implements Validator<RepositorerFindDtos> {

    @Value("${no.records.to.process.threshold}")
    private String noOfRecordsToProcessThreshold;

    private int noOfRecordsToProcessThresholdInt;

    @PostConstruct
    public void init() {
        noOfRecordsToProcessThresholdInt = Integer.parseInt(noOfRecordsToProcessThreshold);
    }

    @Override
    public void validate(RepositorerFindDtos dtos) throws RepositorerValidationException {

        // first validation...
        if (dtos == null || dtos.getRepositorerFindDtos() == null || dtos.getRepositorerFindDtos().isEmpty()) {
            log.error("RepositorerFindDtosValidator#validate --- error occurred (first validation) --- repositorerFindDtos = " + dtos);
            throw new RepositorerValidationException(RepositorerError.INVALID_DATA_ERROR, RepositorerError.INVALID_DATA_ERROR.getMessage());
        }

        // second validation...
        if (dtos.getRepositorerFindDtos().size() > noOfRecordsToProcessThresholdInt) {
            log.error("RepositorerFindDtosValidator#validate --- error occurred (second validation) --- repositorerFindDtos = " + dtos);
            throw new RepositorerValidationException(
                    RepositorerError.LIMIT_FOR_NUMBER_OF_TOTAL_RECORDS_TO_PROCESS_EXCEEDED,
                    RepositorerError.LIMIT_FOR_NUMBER_OF_TOTAL_RECORDS_TO_PROCESS_EXCEEDED.getMessage());
        }

        // third validation...
        for (RepositorerFindDto repositorerFindDto : dtos.getRepositorerFindDtos()) {
            boolean validationError = Stream
                    .of(repositorerFindDto.getFirstname(), repositorerFindDto.getSurname())
                    .anyMatch(d -> d == null || "".equals(d));

            if (validationError) {
                log.error("RepositorerFindDtosValidator#validate --- error occurred (third validation) --- repositorerFindDtos = " + dtos);
                throw new RepositorerValidationException(RepositorerError.INVALID_DATA_ERROR, RepositorerError.INVALID_DATA_ERROR.getMessage());
            }
        }
    }
}
