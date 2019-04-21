package com.eresearch.repositorer.service;

import com.eresearch.repositorer.dto.repositorer.request.RepositorerFindDto;
import com.eresearch.repositorer.dto.repositorer.request.RepositorerFindDtos;
import com.eresearch.repositorer.exception.business.RepositorerBusinessException;
import com.eresearch.repositorer.exception.error.RepositorerError;
import com.eresearch.repositorer.extractor.BatchExtractor;
import com.eresearch.repositorer.gateway.AuthorExtractor;
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
    private BatchExtractor batchExtractor;

    @Override
    public void extractAuthorInfo(final RepositorerFindDto dto, String transactionId) {

        log.info("RepositorerExtractorImpl --> " + Thread.currentThread().getName());

        final String txId = transactionId == null ? this.transactionId.getTransactionId() : transactionId;

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
