package com.eresearch.repositorer.validator;

import com.eresearch.repositorer.dto.repositorer.request.DiscaredMessageFilenameDto;
import com.eresearch.repositorer.exception.data.RepositorerValidationException;
import com.eresearch.repositorer.exception.error.RepositorerError;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

@Component
@Log4j
public class DiscaredMessageFilenameDtoValidator implements Validator<DiscaredMessageFilenameDto> {

    @Override
    public void validate(DiscaredMessageFilenameDto discaredMessageFilenameDto) throws RepositorerValidationException {

        if (discaredMessageFilenameDto.getFilename() == null
                || discaredMessageFilenameDto.getFilename().isEmpty()) {
            log.error("DiscaredMessageFilenameDtoValidator#validate --- error occurred (first validation) --- discaredMessageFilenameDto = " + discaredMessageFilenameDto);
            throw new RepositorerValidationException(RepositorerError.INVALID_DATA_ERROR, RepositorerError.INVALID_DATA_ERROR.getMessage());
        }

    }
}
