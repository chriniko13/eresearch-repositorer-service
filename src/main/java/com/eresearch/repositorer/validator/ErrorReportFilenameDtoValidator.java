package com.eresearch.repositorer.validator;

import com.eresearch.repositorer.dto.repositorer.request.ErrorReportFilenameDto;
import com.eresearch.repositorer.exception.data.RepositorerValidationException;
import com.eresearch.repositorer.exception.error.RepositorerError;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

@Component
@Log4j
public class ErrorReportFilenameDtoValidator implements Validator<ErrorReportFilenameDto> {

    @Override
    public void validate(ErrorReportFilenameDto data) throws RepositorerValidationException {

        if (data.getFilename() == null
                || data.getFilename().isEmpty()) {

            log.error("ErrorReportFilenameDtoValidator#validate --- error occurred (first validation) --- errorReportFilenameDto = " + data);
            throw new RepositorerValidationException(RepositorerError.INVALID_DATA_ERROR, RepositorerError.INVALID_DATA_ERROR.getMessage());
        }
    }
}
