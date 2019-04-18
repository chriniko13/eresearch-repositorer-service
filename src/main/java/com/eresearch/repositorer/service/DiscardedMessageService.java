package com.eresearch.repositorer.service;

import com.eresearch.repositorer.dto.repositorer.request.DiscardedMessageSearchDto;
import com.eresearch.repositorer.dto.repositorer.request.DiscaredMessageFilenameDto;
import com.eresearch.repositorer.dto.repositorer.response.DiscardedMessageDeleteOperationResultDto;
import com.eresearch.repositorer.dto.repositorer.response.RandomEntriesResponseDto;
import com.eresearch.repositorer.dto.repositorer.response.RetrievedDiscardedMessageDto;

import java.util.Collection;

public interface DiscardedMessageService {


    Collection<RetrievedDiscardedMessageDto> find(DiscardedMessageSearchDto discardedMessageSearchDto, boolean fullFetch);

    Collection<RetrievedDiscardedMessageDto> findAll(boolean fullFetch);

    DiscardedMessageDeleteOperationResultDto deleteAll();

    DiscardedMessageDeleteOperationResultDto delete(DiscaredMessageFilenameDto discaredMessageFilenameDto);

    RandomEntriesResponseDto addRandomEntriesForTesting();

}
