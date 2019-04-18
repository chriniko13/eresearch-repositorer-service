package com.eresearch.repositorer.transformer.results.dblp;

import com.eresearch.repositorer.domain.record.Entry;
import com.eresearch.repositorer.dto.dblp.response.DblpAuthor;
import com.eresearch.repositorer.dto.dblp.response.DblpQueueResultDto;
import com.eresearch.repositorer.dto.dblp.response.DblpResultsDto;
import com.eresearch.repositorer.dto.dblp.response.generated.Dblp;
import com.eresearch.repositorer.exception.business.RepositorerBusinessException;
import com.eresearch.repositorer.exception.error.RepositorerError;
import com.eresearch.repositorer.transformer.dto.TransformedEntriesDto;
import com.eresearch.repositorer.transformer.results.ResultsTransformer;
import com.eresearch.repositorer.transformer.results.dblp.processor.DblpProcessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Log4j
@Component
public class DblpResultsTransfomer implements ResultsTransformer {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private List<DblpProcessor> dblpProcessors;

    @Value("${dblp.entries.processing.multithread.approach}")
    private String isDblpEntriesProcessingMultithreadApproach;

    @Autowired
    @Qualifier("dblpEntriesProcessorsExecutor")
    private ExecutorService dblpEntriesProcessorsExecutor;

    @Override
    public Message<?> transform(Message<?> message) {

        log.info("DblpResultsTransfomer --> " + Thread.currentThread().getName());

        try {

            final DblpQueueResultDto dblpQueueResultDto = deserializeMessage(message);

            final List<Entry> entries = doProcessing(dblpQueueResultDto);

            final String transactionId = dblpQueueResultDto.getTransactionId();

            return MessageBuilder
                    .withPayload(new TransformedEntriesDto(entries))
                    .setHeader(TRANSACTION_ID, transactionId)
                    .build();

        } catch (IOException e) {

            log.error("DblpResultsTransfomer#transform --- error occurred", e);

            throw new RepositorerBusinessException(RepositorerError.COULD_NOT_DESERIALIZE_MESSAGE,
                    RepositorerError.COULD_NOT_DESERIALIZE_MESSAGE.getMessage(),
                    e,
                    this.getClass().getName());

        } catch (RepositorerBusinessException e) {

            log.error("DblpResultsTransfomer#transform --- error occurred", e);

            throw e;
        }
    }

    private DblpQueueResultDto deserializeMessage(Message<?> message) throws IOException {

        final String resultAsString = (String) message.getPayload();
        return objectMapper.readValue(resultAsString, new TypeReference<DblpQueueResultDto>() {
        });
    }

    private List<Entry> doProcessing(DblpQueueResultDto dblpQueueResultDto) throws JsonProcessingException, RepositorerBusinessException {

        final List<Entry> entries = getEntriesListToPopulate();

        DblpResultsDto dblpResultsDto = dblpQueueResultDto.getDblpResultsDto();

        if (!dblpResultsDto.getOperationResult()) {
            throw new RepositorerBusinessException(RepositorerError.COULD_NOT_PERFORM_OPERATION,
                    RepositorerError.COULD_NOT_PERFORM_OPERATION.getMessage(),
                    this.getClass().getName());
        }

        Set<Map.Entry<DblpAuthor, List<Dblp>>> resultsEntrySet = dblpResultsDto.getResults().entrySet();

        for (Map.Entry<DblpAuthor, List<Dblp>> entry : resultsEntrySet) {

            final DblpAuthor dblpAuthor = entry.getKey();

            final List<Dblp> dblps = entry.getValue();

            for (Dblp dblp : dblps) {

                if (!Boolean.valueOf(isDblpEntriesProcessingMultithreadApproach)) {
                    doSingleThreadProcessing(entries, dblpAuthor, dblp);
                } else {
                    doMultithreadProcessing(entries, dblpAuthor, dblp);
                }
            }
        }

        return entries;
    }

    private List<Entry> getEntriesListToPopulate() {
        return Boolean.valueOf(isDblpEntriesProcessingMultithreadApproach) ? Collections.synchronizedList(new ArrayList<>()) : new ArrayList<>();
    }

    private void doSingleThreadProcessing(List<Entry> entries, DblpAuthor dblpAuthor, Dblp dblp) throws JsonProcessingException, RepositorerBusinessException {
        for (DblpProcessor dblpProcessor : dblpProcessors) {

            if (dblpProcessor.allowProcessing()) {
                dblpProcessor.doProcessing(entries, dblp, dblpAuthor);
            }

        }
    }

    private void doMultithreadProcessing(List<Entry> entries, DblpAuthor dblpAuthor, Dblp dblp) {

        // dispatch work...
        final List<CompletableFuture<Boolean>> workers = dblpProcessors
                .stream()
                .map(dblpProcessor ->
                        CompletableFuture.supplyAsync(() -> {
                                    try {
                                        dblpProcessor.doProcessing(entries, dblp, dblpAuthor);
                                        return true;
                                    } catch (JsonProcessingException | RepositorerBusinessException e) {
                                        log.error("DblpResultsTransfomer#doMultithreadProcessing --- error occurred.", e);
                                        return false;
                                    }
                                },
                                dblpEntriesProcessorsExecutor
                        )
                )
                .collect(Collectors.toList());

        // collect the results...
        try {
            boolean operationFinishedWithSuccess = true;

            for (CompletableFuture<Boolean> worker : workers) {
                Boolean result = worker.get();
                operationFinishedWithSuccess = operationFinishedWithSuccess && result;
            }

            if (!operationFinishedWithSuccess) {
                throw new RepositorerBusinessException(RepositorerError.COULD_NOT_PERFORM_OPERATION,
                        RepositorerError.COULD_NOT_PERFORM_OPERATION.getMessage(),
                        this.getClass().getName());
            }

        } catch (InterruptedException | ExecutionException e) {
            log.error("DblpResultsTransfomer#doMultithreadProcessing --- error occurred.", e);

            throw new RepositorerBusinessException(RepositorerError.COULD_NOT_PERFORM_OPERATION,
                    RepositorerError.COULD_NOT_PERFORM_OPERATION.getMessage(),
                    this.getClass().getName());
        }

    }
}
