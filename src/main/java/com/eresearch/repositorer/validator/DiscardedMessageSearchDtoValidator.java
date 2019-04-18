package com.eresearch.repositorer.validator;

import com.eresearch.repositorer.dto.repositorer.request.DiscardedMessageSearchDto;
import com.eresearch.repositorer.exception.data.RepositorerValidationException;
import com.eresearch.repositorer.exception.error.RepositorerError;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

/*
    Note: only time can be null.
 */
@Component
@Log4j
public class DiscardedMessageSearchDtoValidator implements Validator<DiscardedMessageSearchDto> {

    @Override
    public void validate(DiscardedMessageSearchDto data) throws RepositorerValidationException {

        if (data.getDate() == null) {
            log.error("DiscardedMessageSearchDtoValidator#validate --- error occurred (first validation) --- discardedMessageSearchDto = " + data);
            throw new RepositorerValidationException(RepositorerError.INVALID_DATA_ERROR, RepositorerError.INVALID_DATA_ERROR.getMessage());
        }
    }
}
