package com.eresearch.repositorer.validator;

import com.eresearch.repositorer.dto.repositorer.request.ErrorReportSearchDto;
import com.eresearch.repositorer.exception.data.RepositorerValidationException;
import com.eresearch.repositorer.exception.error.RepositorerError;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

/*
    Note: only time can be null.
 */
@Log4j
@Component
public class ErrorReportSearchDtoValidator implements Validator<ErrorReportSearchDto> {

    @Override
    public void validate(ErrorReportSearchDto data) throws RepositorerValidationException {

        if (data.getDate() == null) {
            log.error("ErrorReportSearchDtoValidator#validate --- error occurred (first validation) --- errorReportSearchDto = " + data);
            throw new RepositorerValidationException(RepositorerError.INVALID_DATA_ERROR, RepositorerError.INVALID_DATA_ERROR.getMessage());
        }
    }
}
