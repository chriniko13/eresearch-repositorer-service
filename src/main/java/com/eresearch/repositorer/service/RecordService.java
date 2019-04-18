package com.eresearch.repositorer.service;


import com.eresearch.repositorer.dto.repositorer.request.RecordFilenameDto;
import com.eresearch.repositorer.dto.repositorer.request.RecordSearchDto;
import com.eresearch.repositorer.dto.repositorer.response.RecordDeleteOperationResultDto;
import com.eresearch.repositorer.dto.repositorer.response.RecordSearchResultDto;

public interface RecordService {

    RecordSearchResultDto find(RecordSearchDto recordSearchDto, boolean fullFetch);

    RecordSearchResultDto findAll(boolean fullFetch);

    RecordDeleteOperationResultDto deleteAll();

    RecordDeleteOperationResultDto delete(RecordFilenameDto recordFilenameDto);

    RecordSearchResultDto find(RecordFilenameDto recordFilenameDto, boolean fullFetch);
}
