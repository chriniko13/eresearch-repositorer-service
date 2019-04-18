package com.eresearch.repositorer.exception.error;

import java.io.Serializable;

public enum RepositorerError implements Serializable {

    // --- data validation erros ---
    INVALID_DATA_ERROR("Submission of invalid data."),
    LIMIT_FOR_NUMBER_OF_TOTAL_RECORDS_TO_PROCESS_EXCEEDED("Limit for number of total records to process exceeded."),

    // --- business errors ---
    CONNECTOR_CONNECTION_ERROR("Could not connect with external service."),
    COULD_NOT_PERFORM_OPERATION("Could not perform business operation."),
    TOO_MANY_EXTRACTION_PROCESSES_ACTIVE("Too many extraction processes are active, please try again later."),
    NO_UNIQUE_RESULTS_EXIST_BASED_ON_PROVIDED_DISCRIMINATOR("No unique results exist based on provided discriminator."),
    RECORD_DOES_NOT_EXIST("Record does not exist."),
    REPOSITORY_IS_EMPTY("Repository is empty, does not contain any record."),

    // --- critical-fatal errors ---
    COULD_NOT_DESERIALIZE_MESSAGE("Could not deserialize message."),
    COULD_NOT_SERIALIZE_OBJECT("Could not serialize object."),
    COULD_NOT_DESERIALIZE_OBJECT("Could not deserialize object."),
    APPLICATION_NOT_IN_CORRECT_STATE("Application not in correct state."),

    // --- not specified errors ---
    UNIDENTIFIED_ERROR("Unidentified error occurred.");

    private final String message;

    RepositorerError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
