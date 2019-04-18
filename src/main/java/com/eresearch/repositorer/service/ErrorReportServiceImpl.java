package com.eresearch.repositorer.service;

import com.eresearch.repositorer.domain.error.ErrorReport;
import com.eresearch.repositorer.dto.repositorer.request.ErrorReportFilenameDto;
import com.eresearch.repositorer.dto.repositorer.request.ErrorReportSearchDto;
import com.eresearch.repositorer.dto.repositorer.response.ErrorReportDeleteOperationResultDto;
import com.eresearch.repositorer.dto.repositorer.response.ErrorReportDeleteOperationStatus;
import com.eresearch.repositorer.dto.repositorer.response.RandomEntriesResponseDto;
import com.eresearch.repositorer.dto.repositorer.response.RetrievedErrorReportDto;
import com.eresearch.repositorer.exception.business.RepositorerBusinessException;
import com.eresearch.repositorer.exception.error.RepositorerError;
import com.eresearch.repositorer.repository.ErrorReportRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

@Log4j
@Service
public class ErrorReportServiceImpl implements ErrorReportService {

    private static final String SAMPLE_ENTRIES_STORED_SUCCESSFULLY_TO_DB = "Sample entries stored successfully to db.";

    @Autowired
    private ErrorReportRepository errorReportRepository;

    @Autowired
    private Clock clock;

    @Autowired
    private RandomStringGeneratorService randomStringGeneratorService;

    @Override
    public Collection<RetrievedErrorReportDto> find(ErrorReportSearchDto errorReportSearchDto, boolean fullFetch) {
        return errorReportRepository.find(fullFetch, errorReportSearchDto);
    }

    @Override
    public Collection<RetrievedErrorReportDto> findAll(boolean fullFetch) {
        return errorReportRepository.findAll(fullFetch);
    }

    @Override
    public RandomEntriesResponseDto addRandomEntriesForTesting() {

        for (int i = 1; i <= 20; i++) {

            ErrorReport errorReport = ErrorReport.builder()
                    .crashedComponentName(randomStringGeneratorService.randomString(3000))
                    .createdAt(Instant.now(clock))
                    .errorStacktrace(randomStringGeneratorService.randomString(3000))
                    .exceptionToString(randomStringGeneratorService.randomString(3000))
                    .failedMessage(randomStringGeneratorService.randomString(3000))
                    .id(UUID.randomUUID().toString())
                    .repositorerError(RepositorerError.UNIDENTIFIED_ERROR)
                    .build();

            errorReportRepository.store(errorReport);
        }

        return new RandomEntriesResponseDto(SAMPLE_ENTRIES_STORED_SUCCESSFULLY_TO_DB);
    }

    @Override
    public ErrorReportDeleteOperationResultDto deleteAll() {

        try {

            boolean result = errorReportRepository.deleteAll();
            return new ErrorReportDeleteOperationResultDto(result, ErrorReportDeleteOperationStatus.SUCCESS);

        } catch (RepositorerBusinessException e) {

            log.error("ErrorReportServiceImpl#deleteAll --- error occurred.", e);

            final ErrorReportDeleteOperationStatus statusToReturn;
            switch (e.getRepositorerError()) {

                case REPOSITORY_IS_EMPTY:
                    statusToReturn = ErrorReportDeleteOperationStatus.REPOSITORY_IS_EMPTY;
                    break;

                default: //Note: we should never go here....
                    statusToReturn = ErrorReportDeleteOperationStatus.APPLICATION_NOT_IN_CORRECT_STATE;
            }

            return new ErrorReportDeleteOperationResultDto(false, statusToReturn);

        }
    }

    @Override
    public ErrorReportDeleteOperationResultDto delete(ErrorReportFilenameDto errorReportFilenameDto) {
        try {

            String filename = errorReportFilenameDto.getFilename();
            boolean deleteOperationResult = errorReportRepository.delete(filename);

            return new ErrorReportDeleteOperationResultDto(deleteOperationResult, ErrorReportDeleteOperationStatus.SUCCESS);

        } catch (RepositorerBusinessException e) {

            log.error("ErrorReportServiceImpl#delete --- error occurred.", e);

            final ErrorReportDeleteOperationStatus statusToReturn;
            switch (e.getRepositorerError()) {

                case RECORD_DOES_NOT_EXIST:
                    statusToReturn = ErrorReportDeleteOperationStatus.RECORD_DOES_NOT_EXIST;
                    break;

                case NO_UNIQUE_RESULTS_EXIST_BASED_ON_PROVIDED_DISCRIMINATOR:
                    statusToReturn = ErrorReportDeleteOperationStatus.NO_UNIQUE_RESULTS_EXIST_BASED_ON_PROVIDED_DISCRIMINATOR;
                    break;

                default: //Note: we should never go here....
                    statusToReturn = ErrorReportDeleteOperationStatus.APPLICATION_NOT_IN_CORRECT_STATE;
            }

            return new ErrorReportDeleteOperationResultDto(false, statusToReturn);
        }
    }
}
