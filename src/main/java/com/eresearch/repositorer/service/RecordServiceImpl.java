package com.eresearch.repositorer.service;

import com.eresearch.repositorer.domain.record.Author;
import com.eresearch.repositorer.dto.repositorer.request.RecordFilenameDto;
import com.eresearch.repositorer.dto.repositorer.request.RecordSearchDto;
import com.eresearch.repositorer.dto.repositorer.response.RecordDeleteOperationResultDto;
import com.eresearch.repositorer.dto.repositorer.response.RecordDeleteOperationStatus;
import com.eresearch.repositorer.dto.repositorer.response.RecordSearchResultDto;
import com.eresearch.repositorer.dto.repositorer.response.RetrievedRecordDto;
import com.eresearch.repositorer.exception.business.RepositorerBusinessException;
import com.eresearch.repositorer.repository.RecordRepository;
import lombok.extern.log4j.Log4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j
public class RecordServiceImpl implements RecordService {

    private final RecordRepository recordRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public RecordServiceImpl(RecordRepository recordRepository, ModelMapper modelMapper) {
        this.recordRepository = recordRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RecordSearchResultDto find(RecordSearchDto recordSearchDto, boolean fullFetch) {

        Author author = modelMapper.map(recordSearchDto, Author.class);

        Collection<RetrievedRecordDto> retrievedRecordDtos = recordRepository.find(fullFetch, author);

        return new RecordSearchResultDto(retrievedRecordDtos);
    }

    @Override
    public RecordSearchResultDto findAll(boolean fullFetch) {
        Collection<RetrievedRecordDto> retrievedRecordDtos = recordRepository.findAll(fullFetch);

        List<RetrievedRecordDto> fromEarliestToLatestRecordDtos = retrievedRecordDtos
                .stream()
                .sorted(earliestToLatestRecordsComparator())
                .collect(Collectors.toList());

        return new RecordSearchResultDto(fromEarliestToLatestRecordDtos);
    }

    @Override
    public RecordDeleteOperationResultDto deleteAll() {
        try {

            boolean result = recordRepository.deleteAll();
            return new RecordDeleteOperationResultDto(result, RecordDeleteOperationStatus.SUCCESS);

        } catch (RepositorerBusinessException e) {

            final RecordDeleteOperationStatus statusToReturn;
            switch (e.getRepositorerError()) {

                case REPOSITORY_IS_EMPTY:
                    statusToReturn = RecordDeleteOperationStatus.REPOSITORY_IS_EMPTY;
                    break;

                default: //Note: we should never go here....
                    statusToReturn = RecordDeleteOperationStatus.APPLICATION_NOT_IN_CORRECT_STATE;
            }

            return new RecordDeleteOperationResultDto(false, statusToReturn);

        }
    }

    @Override
    public RecordDeleteOperationResultDto delete(RecordFilenameDto recordFilenameDto) {
        try {

            String filename = recordFilenameDto.getFilename();
            boolean deleteOperationResult = recordRepository.delete(filename);

            return new RecordDeleteOperationResultDto(deleteOperationResult, RecordDeleteOperationStatus.SUCCESS);

        } catch (RepositorerBusinessException e) {

            final RecordDeleteOperationStatus statusToReturn;
            switch (e.getRepositorerError()) {

                case RECORD_DOES_NOT_EXIST:
                    statusToReturn = RecordDeleteOperationStatus.RECORD_DOES_NOT_EXIST;
                    break;

                case NO_UNIQUE_RESULTS_EXIST_BASED_ON_PROVIDED_DISCRIMINATOR:
                    statusToReturn = RecordDeleteOperationStatus.NO_UNIQUE_RESULTS_EXIST_BASED_ON_PROVIDED_DISCRIMINATOR;
                    break;

                default: //Note: we should never go here....
                    statusToReturn = RecordDeleteOperationStatus.APPLICATION_NOT_IN_CORRECT_STATE;
            }

            return new RecordDeleteOperationResultDto(false, statusToReturn);
        }
    }

    @Override
    public RecordSearchResultDto find(RecordFilenameDto recordFilenameDto, boolean fullFetch) {
        Collection<RetrievedRecordDto> dtos = recordRepository.find(recordFilenameDto.getFilename(), fullFetch);
        return new RecordSearchResultDto(dtos);
    }

    private Comparator<RetrievedRecordDto> earliestToLatestRecordsComparator() {
        return (o1, o2) -> {
            //Note: filename contains info such as: Errikos_NoValue_Ventouras#2017-08-28T01:07:10.590
            String createdAtStr_o1 = o1.getFilename().split("#")[1];
            LocalDateTime localDateTime_o1 = LocalDateTime.parse(createdAtStr_o1);

            String createdAtStr_o2 = o2.getFilename().split("#")[1];
            LocalDateTime localDateTime_o2 = LocalDateTime.parse(createdAtStr_o2);

            return localDateTime_o1.compareTo(localDateTime_o2);
        };
    }
}
