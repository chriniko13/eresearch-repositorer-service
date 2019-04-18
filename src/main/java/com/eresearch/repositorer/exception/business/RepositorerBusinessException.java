package com.eresearch.repositorer.exception.business;

import com.eresearch.repositorer.exception.error.RepositorerError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class RepositorerBusinessException extends RuntimeException {

    private final RepositorerError repositorerError;
    private final String crashedComponentName;

    public RepositorerBusinessException(RepositorerError repositorerError, String message, Throwable cause, String crashedComponentName) {
        super(message, cause);
        this.repositorerError = repositorerError;
        this.crashedComponentName = crashedComponentName;
    }

    public RepositorerBusinessException(RepositorerError repositorerError, String message, String crashedComponentName) {
        super(message);
        this.repositorerError = repositorerError;
        this.crashedComponentName = crashedComponentName;
    }

    public RepositorerBusinessException(RepositorerError repositorerError, String message) {
        this(repositorerError, message, null);
    }

    public RepositorerError getRepositorerError() {
        return repositorerError;
    }

    public String getCrashedComponentName() {
        return crashedComponentName;
    }
}
