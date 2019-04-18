package com.eresearch.repositorer.service;

import com.eresearch.repositorer.domain.common.NameVariant;
import com.eresearch.repositorer.domain.lookup.NameLookup;
import com.eresearch.repositorer.domain.lookup.NameLookupStatus;
import com.eresearch.repositorer.dto.repositorer.request.NameLookupSearchDto;
import com.eresearch.repositorer.dto.repositorer.response.NameLookupDeleteOperationResultDto;
import com.eresearch.repositorer.dto.repositorer.response.RandomEntriesResponseDto;
import com.eresearch.repositorer.exception.business.RepositorerBusinessException;
import com.eresearch.repositorer.repository.NamesLookupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Service
public class NameLookupServiceImpl implements NameLookupService {

    private static final String SAMPLE_ENTRIES_STORED_SUCCESSFULLY_TO_DB = "Sample entries stored successfully to db.";

    @Autowired
    private Clock clock;

    @Autowired
    private NamesLookupRepository namesLookupRepository;

    @Override
    public NameLookup find(NameLookupSearchDto nameLookupSearchDto) {
        return namesLookupRepository.findOne(nameLookupSearchDto.getId());
    }

    @Override
    public Collection<NameLookup> findAll() {
        return namesLookupRepository.findAll();
    }

    @Override
    public RandomEntriesResponseDto addRandomEntriesForTesting() {
        for (int i = 1; i <= 20; i++) {

            String uuid = UUID.randomUUID().toString();

            NameLookup nameLookup = new NameLookup(UUID.randomUUID().toString(),
                    "Firstname" + uuid,
                    "Initials" + uuid,
                    "Surname" + uuid,
                    Collections.singletonList(new NameVariant("Firstname" + uuid,
                            "Initials" + uuid,
                            "Surname" + uuid)),
                    NameLookupStatus.PENDING,
                    Instant.now(clock));

            namesLookupRepository.save(nameLookup);
        }

        return new RandomEntriesResponseDto(SAMPLE_ENTRIES_STORED_SUCCESSFULLY_TO_DB);
    }

    @Override
    public NameLookupDeleteOperationResultDto deleteAll() {

        try {

            namesLookupRepository.deleteAll();
            return new NameLookupDeleteOperationResultDto(true);

        } catch (RepositorerBusinessException e) {

            return new NameLookupDeleteOperationResultDto(false);
        }
    }

    @Override
    public NameLookupDeleteOperationResultDto delete(NameLookupSearchDto nameLookupSearchDto) {
        try {

            namesLookupRepository.delete(nameLookupSearchDto.getId());
            return new NameLookupDeleteOperationResultDto(true);

        } catch (RepositorerBusinessException e) {

            return new NameLookupDeleteOperationResultDto(false);
        }
    }
}
