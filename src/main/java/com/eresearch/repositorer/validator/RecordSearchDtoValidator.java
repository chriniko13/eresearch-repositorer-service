package com.eresearch.repositorer.validator;

import com.eresearch.repositorer.dto.repositorer.request.RecordSearchDto;
import com.eresearch.repositorer.exception.data.RepositorerValidationException;
import com.eresearch.repositorer.exception.error.RepositorerError;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

/*
    Note: Only initials could be empty or null.
 */
@Component
@Log4j
public class RecordSearchDtoValidator implements Validator<RecordSearchDto> {

    @Override
    public void validate(RecordSearchDto data) throws RepositorerValidationException {

        boolean validationError = Stream
                .of(data.getFirstname(), data.getSurname())
                .anyMatch(d -> d == null || "".equals(d));

        if (validationError) {
            log.error("RecordSearchDtoValidator#validate --- error occurred (first validation) --- recordSearchDto = " + data);
            throw new RepositorerValidationException(RepositorerError.INVALID_DATA_ERROR, RepositorerError.INVALID_DATA_ERROR.getMessage());
        }

    }
}
