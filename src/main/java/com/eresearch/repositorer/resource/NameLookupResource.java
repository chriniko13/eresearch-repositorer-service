package com.eresearch.repositorer.resource;

import com.eresearch.repositorer.dto.repositorer.request.NameLookupSearchDto;
import com.eresearch.repositorer.dto.repositorer.response.NameLookupDeleteOperationResultDto;
import com.eresearch.repositorer.dto.repositorer.response.RandomEntriesResponseDto;
import com.eresearch.repositorer.dto.repositorer.response.RetrievedNameLookupDtos;
import com.eresearch.repositorer.exception.data.RepositorerValidationException;
import org.springframework.http.ResponseEntity;

public interface NameLookupResource {

    ResponseEntity<RetrievedNameLookupDtos> find(NameLookupSearchDto nameLookupSearchDto) throws RepositorerValidationException;

    ResponseEntity<RetrievedNameLookupDtos> findAll();

    ResponseEntity<RandomEntriesResponseDto> addRandomEntriesForTesting();

    ResponseEntity<NameLookupDeleteOperationResultDto> deleteAll();

    ResponseEntity<NameLookupDeleteOperationResultDto> delete(NameLookupSearchDto nameLookupSearchDto) throws RepositorerValidationException;

}
