package com.eresearch.repositorer.resource;

import com.eresearch.repositorer.dto.repositorer.request.ErrorReportFilenameDto;
import com.eresearch.repositorer.dto.repositorer.request.ErrorReportSearchDto;
import com.eresearch.repositorer.dto.repositorer.response.ErrorReportDeleteOperationResultDto;
import com.eresearch.repositorer.dto.repositorer.response.RandomEntriesResponseDto;
import com.eresearch.repositorer.dto.repositorer.response.RetrievedErrorReportDtos;
import com.eresearch.repositorer.exception.data.RepositorerValidationException;
import org.springframework.http.ResponseEntity;

public interface ErrorReportResource {

    ResponseEntity<RetrievedErrorReportDtos> find(ErrorReportSearchDto errorReportSearchDto, boolean fullFetch) throws RepositorerValidationException;

    ResponseEntity<RetrievedErrorReportDtos> findAll(boolean fullFetch);

    ResponseEntity<RandomEntriesResponseDto> addRandomEntriesForTesting();

    ResponseEntity<ErrorReportDeleteOperationResultDto> deleteAll();

    ResponseEntity<ErrorReportDeleteOperationResultDto> delete(ErrorReportFilenameDto errorReportFilenameDto) throws RepositorerValidationException;

}
