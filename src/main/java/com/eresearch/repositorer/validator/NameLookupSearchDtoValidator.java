package com.eresearch.repositorer.validator;

import com.eresearch.repositorer.dto.repositorer.request.NameLookupSearchDto;
import com.eresearch.repositorer.exception.data.RepositorerValidationException;
import com.eresearch.repositorer.exception.error.RepositorerError;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

@Component
@Log4j
public class NameLookupSearchDtoValidator implements Validator<NameLookupSearchDto> {

    @Override
    public void validate(NameLookupSearchDto data) throws RepositorerValidationException {

        if (data.getId() == null || data.getId().isEmpty()) {
            log.error("NameLookupSearchDtoValidator#validate --- error occurred (first validation) --- nameLookupSearchDto = " + data);
            throw new RepositorerValidationException(RepositorerError.INVALID_DATA_ERROR, RepositorerError.INVALID_DATA_ERROR.getMessage());
        }
    }
}
