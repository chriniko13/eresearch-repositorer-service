package com.eresearch.repositorer.service;

import com.eresearch.repositorer.dto.repositorer.request.ErrorReportFilenameDto;
import com.eresearch.repositorer.dto.repositorer.request.ErrorReportSearchDto;
import com.eresearch.repositorer.dto.repositorer.response.ErrorReportDeleteOperationResultDto;
import com.eresearch.repositorer.dto.repositorer.response.RandomEntriesResponseDto;
import com.eresearch.repositorer.dto.repositorer.response.RetrievedErrorReportDto;

import java.util.Collection;

public interface ErrorReportService {

    Collection<RetrievedErrorReportDto> find(ErrorReportSearchDto errorReportSearchDto, boolean fullFetch);

    Collection<RetrievedErrorReportDto> findAll(boolean fullFetch);

    RandomEntriesResponseDto addRandomEntriesForTesting();

    ErrorReportDeleteOperationResultDto deleteAll();

    ErrorReportDeleteOperationResultDto delete(ErrorReportFilenameDto errorReportFilenameDto);

}
