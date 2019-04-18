package com.eresearch.repositorer.extractor;

import com.eresearch.repositorer.dto.repositorer.request.RepositorerFindDto;
import com.eresearch.repositorer.dto.repositorer.request.RepositorerFindDtos;
import com.eresearch.repositorer.event.BatchExtractorWakeUpEvent;
import com.eresearch.repositorer.gateway.AuthorExtractor;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

@Log4j
@Component
public class BatchExtractor implements BatchExtractorMBean {

    @Value("${no.active.extraction.processes}")
    private String noActiveExtractionProcessesStr;

    private int noActiveExtractionProcesses;

    private LinkedBlockingDeque<RepositorerFindDto> repositorerFindDtosQueue;

    @Autowired
    private AuthorExtractor authorExtractor;

    @Autowired
    private EventBus batchExtractorEventBus;

    @PostConstruct
    public void init() {
        noActiveExtractionProcesses = Integer.parseInt(noActiveExtractionProcessesStr);

        repositorerFindDtosQueue = new LinkedBlockingDeque<>(noActiveExtractionProcesses);

        batchExtractorEventBus.register(this);
    }

    @Override
    public List<String> getEntriesWaitingToBeProcessed() {
        return repositorerFindDtosQueue.stream().map(RepositorerFindDto::toString).collect(Collectors.toList());
    }

    public void handleExtraction(RepositorerFindDtos dtos) {
        //add them to queue so the worker will process them in the future...
        List<RepositorerFindDto> repositorerFindDtos = dtos.getRepositorerFindDtos();
        repositorerFindDtosQueue.addAll(repositorerFindDtos);

        //fire one extraction...
        fireExtraction();
    }

    public boolean canAcceptIncomingExtraction() {
        return repositorerFindDtosQueue.size() != noActiveExtractionProcesses;
    }

    private void fireExtraction() {
        try {

            RepositorerFindDto repositorerFindDto = repositorerFindDtosQueue.take();

            final String txId = UUID.randomUUID().toString();
            authorExtractor.extract(repositorerFindDto, txId);
            log.info("BatchExtractor#BatchExtractorWorker#fireExtraction(Void) --- fired, with parameters: dto = "
                    + repositorerFindDto
                    + ", txId = "
                    + txId);

        } catch (InterruptedException e) {
            log.error("BatchExtractor#fireExtraction(Void) --- error occurred.", e);
        }
    }

    /*
        Note: this method is fired from the event which: com.eresearch.repositorer.activator.TransformedResultsPersister posts.
     */
    @Subscribe
    public void fireExtraction(BatchExtractorWakeUpEvent event) {
        try {
            if (repositorerFindDtosQueue.isEmpty()) {
                return;
            }

            if (!event.isContinueWork()) {
                return;
            }

            RepositorerFindDto repositorerFindDto = repositorerFindDtosQueue.take();

            final String txId = UUID.randomUUID().toString();
            authorExtractor.extract(repositorerFindDto, txId);
            log.info("BatchExtractor#BatchExtractorWorker#fireExtraction(BatchExtractorWakeUpEvent) --- fired, with parameters: dto = "
                    + repositorerFindDto
                    + ", txId = "
                    + txId);

        } catch (InterruptedException e) {
            log.error("BatchExtractor#fireExtraction(BatchExtractorWakeUpEvent) --- error occurred.", e);
        }
    }

}
