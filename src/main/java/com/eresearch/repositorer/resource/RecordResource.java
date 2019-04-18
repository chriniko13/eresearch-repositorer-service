package com.eresearch.repositorer.resource;


import com.eresearch.repositorer.dto.repositorer.request.RecordFilenameDto;
import com.eresearch.repositorer.dto.repositorer.request.RecordSearchDto;
import com.eresearch.repositorer.dto.repositorer.response.RecordDeleteOperationResultDto;
import com.eresearch.repositorer.dto.repositorer.response.RecordSearchResultDto;
import com.eresearch.repositorer.exception.data.RepositorerValidationException;
import org.springframework.http.ResponseEntity;

public interface RecordResource {

    ResponseEntity<RecordSearchResultDto> find(RecordSearchDto recordSearchDto, boolean fullFetch) throws RepositorerValidationException;

    ResponseEntity<RecordSearchResultDto> find(RecordFilenameDto recordFilenameDto, boolean fullFetch) throws RepositorerValidationException;

    ResponseEntity<RecordSearchResultDto> findAll(boolean fullFetch);

    ResponseEntity<RecordDeleteOperationResultDto> deleteAll();

    ResponseEntity<RecordDeleteOperationResultDto> delete(RecordFilenameDto recordFilenameDto) throws RepositorerValidationException;
}
