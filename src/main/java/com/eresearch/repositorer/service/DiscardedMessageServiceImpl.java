package com.eresearch.repositorer.service;

import com.eresearch.repositorer.domain.discard.DiscardedMessage;
import com.eresearch.repositorer.dto.repositorer.request.DiscardedMessageSearchDto;
import com.eresearch.repositorer.dto.repositorer.request.DiscaredMessageFilenameDto;
import com.eresearch.repositorer.dto.repositorer.response.DiscardedMessageDeleteOperationResultDto;
import com.eresearch.repositorer.dto.repositorer.response.DiscardedMessageDeleteOperationStatus;
import com.eresearch.repositorer.dto.repositorer.response.RandomEntriesResponseDto;
import com.eresearch.repositorer.dto.repositorer.response.RetrievedDiscardedMessageDto;
import com.eresearch.repositorer.exception.business.RepositorerBusinessException;
import com.eresearch.repositorer.repository.DiscardedMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

@Service
public class DiscardedMessageServiceImpl implements DiscardedMessageService {

    private static final String SAMPLE_ENTRIES_STORED_SUCCESSFULLY_TO_DB = "Sample entries stored successfully to db.";

    private final DiscardedMessageRepository discardedMessageRepository;
    private final Clock clock;
    private final RandomStringGeneratorService randomStringGeneratorService;

    @Autowired
    public DiscardedMessageServiceImpl(DiscardedMessageRepository discardedMessageRepository, Clock clock, RandomStringGeneratorService randomStringGeneratorService) {
        this.discardedMessageRepository = discardedMessageRepository;
        this.clock = clock;
        this.randomStringGeneratorService = randomStringGeneratorService;
    }

    @Override
    public Collection<RetrievedDiscardedMessageDto> find(DiscardedMessageSearchDto discardedMessageSearchDto, boolean fullFetch) {
        return discardedMessageRepository.find(fullFetch, discardedMessageSearchDto);
    }

    @Override
    public Collection<RetrievedDiscardedMessageDto> findAll(boolean fullFetch) {
        return discardedMessageRepository.findAll(fullFetch);
    }

    @Override
    public DiscardedMessageDeleteOperationResultDto deleteAll() {

        try {

            boolean result = discardedMessageRepository.deleteAll();
            return new DiscardedMessageDeleteOperationResultDto(result, DiscardedMessageDeleteOperationStatus.SUCCESS);

        } catch (RepositorerBusinessException e) {

            final DiscardedMessageDeleteOperationStatus statusToReturn;
            switch (e.getRepositorerError()) {

                case REPOSITORY_IS_EMPTY:
                    statusToReturn = DiscardedMessageDeleteOperationStatus.REPOSITORY_IS_EMPTY;
                    break;

                default: //Note: we should never go here....
                    statusToReturn = DiscardedMessageDeleteOperationStatus.APPLICATION_NOT_IN_CORRECT_STATE;
            }

            return new DiscardedMessageDeleteOperationResultDto(false, statusToReturn);
        }
    }

    @Override
    public DiscardedMessageDeleteOperationResultDto delete(DiscaredMessageFilenameDto discaredMessageFilenameDto) {

        try {

            String filename = discaredMessageFilenameDto.getFilename();
            boolean deleteOperationResult = discardedMessageRepository.delete(filename);

            return new DiscardedMessageDeleteOperationResultDto(deleteOperationResult, DiscardedMessageDeleteOperationStatus.SUCCESS);

        } catch (RepositorerBusinessException e) {

            final DiscardedMessageDeleteOperationStatus statusToReturn;
            switch (e.getRepositorerError()) {

                case RECORD_DOES_NOT_EXIST:
                    statusToReturn = DiscardedMessageDeleteOperationStatus.RECORD_DOES_NOT_EXIST;
                    break;

                case NO_UNIQUE_RESULTS_EXIST_BASED_ON_PROVIDED_DISCRIMINATOR:
                    statusToReturn = DiscardedMessageDeleteOperationStatus.NO_UNIQUE_RESULTS_EXIST_BASED_ON_PROVIDED_DISCRIMINATOR;
                    break;

                default: //Note: we should never go here....
                    statusToReturn = DiscardedMessageDeleteOperationStatus.APPLICATION_NOT_IN_CORRECT_STATE;
            }

            return new DiscardedMessageDeleteOperationResultDto(false, statusToReturn);
        }
    }

    @Override
    public RandomEntriesResponseDto addRandomEntriesForTesting() {

        for (int i = 1; i <= 20; i++) {

            DiscardedMessage discardedMessage = DiscardedMessage.builder()
                    .id(UUID.randomUUID().toString())
                    .createdAt(Instant.now(clock))
                    .messageToString(randomStringGeneratorService.randomString(5000))
                    .build();

            discardedMessageRepository.store(discardedMessage);
        }
        return new RandomEntriesResponseDto(SAMPLE_ENTRIES_STORED_SUCCESSFULLY_TO_DB);
    }

}
