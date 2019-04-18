package com.eresearch.repositorer.resource;

import com.eresearch.repositorer.dto.repositorer.request.DiscardedMessageSearchDto;
import com.eresearch.repositorer.dto.repositorer.request.DiscaredMessageFilenameDto;
import com.eresearch.repositorer.dto.repositorer.response.DiscardedMessageDeleteOperationResultDto;
import com.eresearch.repositorer.dto.repositorer.response.RandomEntriesResponseDto;
import com.eresearch.repositorer.dto.repositorer.response.RetrievedDiscardedMessageDtos;
import com.eresearch.repositorer.exception.data.RepositorerValidationException;
import org.springframework.http.ResponseEntity;

public interface DiscardedMessageResource {

    ResponseEntity<RetrievedDiscardedMessageDtos> find(DiscardedMessageSearchDto discardedMessageSearchDto, boolean fullFetch) throws RepositorerValidationException;

    ResponseEntity<RetrievedDiscardedMessageDtos> findAll(boolean fullFetch);

    ResponseEntity<DiscardedMessageDeleteOperationResultDto> deleteAll();

    ResponseEntity<DiscardedMessageDeleteOperationResultDto> delete(DiscaredMessageFilenameDto discaredMessageFilenameDto) throws RepositorerValidationException;

    ResponseEntity<RandomEntriesResponseDto> addRandomEntriesForTesting();
}
