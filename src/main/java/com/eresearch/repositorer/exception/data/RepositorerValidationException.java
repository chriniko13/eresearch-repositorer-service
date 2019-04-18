package com.eresearch.repositorer.exception.data;

import com.eresearch.repositorer.exception.error.RepositorerError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class RepositorerValidationException extends Exception {

    private final RepositorerError repositorerError;

    public RepositorerValidationException(RepositorerError repositorerError, String message, Throwable cause) {
        super(message, cause);
        this.repositorerError = repositorerError;
    }

    public RepositorerValidationException(RepositorerError repositorerError, String message) {
        super(message);
        this.repositorerError = repositorerError;
    }

    public RepositorerError getRepositorerError() {
        return repositorerError;
    }
}
