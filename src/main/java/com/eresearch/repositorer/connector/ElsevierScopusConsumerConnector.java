package com.eresearch.repositorer.connector;

import com.eresearch.repositorer.dto.scopus.request.ElsevierScopusConsumerDto;
import com.eresearch.repositorer.dto.scopus.response.ScopusFinderImmediateResultDto;
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
public class ElsevierScopusConsumerConnector implements Connector {

    private static final String TRANSACTION_ID = "Transaction-Id";
    private static final String SCOPUS_CONSUMER_ENDPOINT = "/scopus-consumer/find-q";

    @Value("${elsevier.scopus.consumer.url}")
    private String elsevierScopusConsumerUrl;

    @Autowired
    @Qualifier("repositorerRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    @Qualifier("basicRetryPolicyForConnectors")
    private RetryPolicy basicRetryPolicyForConnectors;

    private ScopusFinderImmediateResultDto extractInfoFromScopus(ElsevierScopusConsumerDto elsevierScopusConsumerDto, String transactionId) {

        final String url = elsevierScopusConsumerUrl + SCOPUS_CONSUMER_ENDPOINT;

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(TRANSACTION_ID, transactionId);

        final HttpEntity<ElsevierScopusConsumerDto> httpEntity
                = new HttpEntity<>(elsevierScopusConsumerDto, httpHeaders);

        final ResponseEntity<ScopusFinderImmediateResultDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                httpEntity,
                ScopusFinderImmediateResultDto.class);

        return responseEntity.getBody();
    }

    public ScopusFinderImmediateResultDto extractInfoFromScopusWithRetries(ElsevierScopusConsumerDto elsevierScopusConsumerDto, String transactionId) {

        return Failsafe
                .with(basicRetryPolicyForConnectors)
                .withFallback(() -> {
                    throw new RepositorerBusinessException(RepositorerError.CONNECTOR_CONNECTION_ERROR,
                            RepositorerError.CONNECTOR_CONNECTION_ERROR.getMessage(),
                            this.getClass().getName());
                })
                .onSuccess(s -> log.info("ElsevierScopusConsumerConnector#extractInfoFromScopusWithRetries, completed successfully!"))
                .onFailure(error -> log.error("ElsevierScopusConsumerConnector#extractInfoFromScopusWithRetries, failed!"))
                .onAbort(error -> log.error("ElsevierScopusConsumerConnector#extractInfoFromScopusWithRetries, aborted!"))
                .get((context) -> {

                    long startTime = context.getStartTime().toMillis();
                    long elapsedTime = context.getElapsedTime().toMillis();
                    int executions = context.getExecutions();

                    String message = String.format("ElsevierScopusConsumerConnector#extractInfoFromScopusWithRetries, retrying...with params: " +
                            "[executions=%s, startTime(ms)=%s, elapsedTime(ms)=%s, elsevierScopusConsumerDto=%s, transactionId=%s]", executions, startTime, elapsedTime, elsevierScopusConsumerDto, transactionId);

                    log.warn(message);

                    return extractInfoFromScopus(elsevierScopusConsumerDto, transactionId);
                });
    }
}
