package com.eresearch.repositorer.service;

import com.eresearch.repositorer.domain.lookup.NameLookup;
import com.eresearch.repositorer.dto.repositorer.request.NameLookupSearchDto;
import com.eresearch.repositorer.dto.repositorer.response.NameLookupDeleteOperationResultDto;
import com.eresearch.repositorer.dto.repositorer.response.RandomEntriesResponseDto;

import java.util.Collection;

public interface NameLookupService {

    NameLookup find(NameLookupSearchDto nameLookupSearchDto);

    Collection<NameLookup> findAll();

    RandomEntriesResponseDto addRandomEntriesForTesting();

    NameLookupDeleteOperationResultDto deleteAll();

    NameLookupDeleteOperationResultDto delete(NameLookupSearchDto nameLookupSearchDto);
}
