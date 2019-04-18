package com.eresearch.repositorer.service;

import com.eresearch.repositorer.dto.repositorer.request.RepositorerFindDto;
import com.eresearch.repositorer.dto.repositorer.request.RepositorerFindDtos;
import com.eresearch.repositorer.exception.business.RepositorerBusinessException;
import com.eresearch.repositorer.exception.error.RepositorerError;
import com.eresearch.repositorer.extractor.BatchExtractor;
import com.eresearch.repositorer.gateway.AuthorExtractor;
import com.eresearch.repositorer.repository.NamesLookupRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j
@Service
public class RepositorerExtractorImpl implements RepositorerExtractor {

    @Autowired
    private TransactionId transactionId;

    @Autowired
    private AuthorExtractor authorExtractor;

    @Autowired
    private NamesLookupRepository namesLookupRepository;

    @Autowired
    private BatchExtractor batchExtractor;

    @Override
    public void extractAuthorInfo(final RepositorerFindDto dto) {

        log.info("RepositorerExtractorImpl --> " + Thread.currentThread().getName());

        final String txId = this.transactionId.getTransactionId();

        authorExtractor.extract(dto, txId);

        log.info("RepositorerExtractorImpl#extractAuthorInfo --- fired, with parameters: dto = "
                + dto
                + ", txId = "
                + txId);

    }

    @Override
    public void extractAuthorInfo(RepositorerFindDtos dtos) {

        // investigate if too many extraction processes are still active, if yes respond accordingly....
        if (!batchExtractor.canAcceptIncomingExtraction()) {
            throw new RepositorerBusinessException(RepositorerError.TOO_MANY_EXTRACTION_PROCESSES_ACTIVE,
                    RepositorerError.TOO_MANY_EXTRACTION_PROCESSES_ACTIVE.getMessage());
        }

        batchExtractor.handleExtraction(dtos);
    }
}
