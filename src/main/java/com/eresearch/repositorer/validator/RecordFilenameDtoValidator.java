package com.eresearch.repositorer.validator;

import com.eresearch.repositorer.dto.repositorer.request.RecordFilenameDto;
import com.eresearch.repositorer.exception.data.RepositorerValidationException;
import com.eresearch.repositorer.exception.error.RepositorerError;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

@Component
@Log4j
public class RecordFilenameDtoValidator implements Validator<RecordFilenameDto> {

    @Override
    public void validate(RecordFilenameDto data) throws RepositorerValidationException {
        if (data.getFilename() == null
                || data.getFilename().isEmpty()) {

            log.error("RecordFilenameDtoValidator#validate --- error occurred (first validation) --- recordFilenameDto = " + data);
            throw new RepositorerValidationException(RepositorerError.INVALID_DATA_ERROR, RepositorerError.INVALID_DATA_ERROR.getMessage());
        }
    }
}
