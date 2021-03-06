package com.eresearch.repositorer.connector;

import com.eresearch.repositorer.dto.dblp.request.DblpConsumerDto;
import com.eresearch.repositorer.dto.dblp.response.DblpImmediateResultDto;
import com.eresearch.repositorer.exception.business.RepositorerBusinessException;
import com.eresearch.repositorer.exception.error.RepositorerError;
import lombok.extern.log4j.Log4j;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Log4j
@Component
public class DblpConsumerConnector implements Connector {

    private static final String TRANSACTION_ID = "Transaction-Id";

    @Value("${dblp.consumer.url}")
    private String dblpConsumerUrl;

    @Autowired
    @Qualifier("repositorerRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    @Qualifier("basicRetryPolicyForConnectors")
    private RetryPolicy basicRetryPolicyForConnectors;

    private DblpImmediateResultDto extractInfoFromDblp(DblpConsumerDto dblpConsumerDto, String transactionId) {

        final String url = dblpConsumerUrl;

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(TRANSACTION_ID, transactionId);

        final HttpEntity<DblpConsumerDto> httpEntity = new HttpEntity<>(dblpConsumerDto, httpHeaders);

        final ResponseEntity<DblpImmediateResultDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                httpEntity,
                DblpImmediateResultDto.class);

        return responseEntity.getBody();
    }

    public DblpImmediateResultDto extractInfoFromDblpWithRetries(DblpConsumerDto dblpConsumerDto, String transactionId) {

        return Failsafe
                .with(basicRetryPolicyForConnectors)
                .withFallback(() -> {
                    throw new RepositorerBusinessException(RepositorerError.CONNECTOR_CONNECTION_ERROR,
                            RepositorerError.CONNECTOR_CONNECTION_ERROR.getMessage(),
                            this.getClass().getName());
                })
                .onSuccess(s -> log.info("DblpConsumerConnector#extractInfoFromDblpWithRetries, completed successfully!"))
                .onFailure(error -> log.error("DblpConsumerConnector#extractInfoFromDblpWithRetries, failed!"))
                .onAbort(error -> log.error("DblpConsumerConnector#extractInfoFromDblpWithRetries, aborted!"))
                .get((context) -> {

                    long startTime = context.getStartTime().toMillis();
                    long elapsedTime = context.getElapsedTime().toMillis();
                    int executions = context.getExecutions();

                    String message = String.format("DblpConsumerConnector#extractInfoFromDblpWithRetries, retrying...with params: " +
                            "[executions=%s, startTime(ms)=%s, elapsedTime(ms)=%s, dblpConsumerDto=%s, transactionId=%s]", executions, startTime, elapsedTime, dblpConsumerDto, transactionId);

                    log.warn(message);

                    return extractInfoFromDblp(dblpConsumerDto, transactionId);
                });
    }
}
