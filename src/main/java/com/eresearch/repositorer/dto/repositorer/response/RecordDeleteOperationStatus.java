package com.eresearch.repositorer.dto.repositorer.response;

public enum RecordDeleteOperationStatus {

    // --- failure values ---
    RECORD_DOES_NOT_EXIST,
    NO_UNIQUE_RESULTS_EXIST_BASED_ON_PROVIDED_DISCRIMINATOR,
    APPLICATION_NOT_IN_CORRECT_STATE,
    REPOSITORY_IS_EMPTY,

    // --- success values ---
    SUCCESS
}
