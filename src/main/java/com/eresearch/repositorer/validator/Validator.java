package com.eresearch.repositorer.validator;

import com.eresearch.repositorer.exception.data.RepositorerValidationException;

public interface Validator<T> {

    void validate(T data) throws RepositorerValidationException;
}
