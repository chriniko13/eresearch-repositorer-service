package com.eresearch.repositorer.connector;

import com.eresearch.repositorer.dto.sciencedirect.request.ElsevierScienceDirectConsumerDto;
import com.eresearch.repositorer.dto.sciencedirect.response.SciDirImmediateResultDto;
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
public class ScienceDirectConsumerConnector implements Connector {

    private static final String TRANSACTION_ID = "Transaction-Id";

    @Value("${sciencedirect.consumer.url}")
    private String scienceDirectConsumerUrl;

    @Autowired
    @Qualifier("repositorerRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    @Qualifier("basicRetryPolicyForConnectors")
    private RetryPolicy basicRetryPolicyForConnectors;

    private SciDirImmediateResultDto extractInfoFromScienceDirect(ElsevierScienceDirectConsumerDto elsevierScienceDirectConsumerDto, String transactionId) {

        final String url = scienceDirectConsumerUrl;

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(TRANSACTION_ID, transactionId);

        final HttpEntity<ElsevierScienceDirectConsumerDto> httpEntity
                = new HttpEntity<>(elsevierScienceDirectConsumerDto, httpHeaders);

        final ResponseEntity<SciDirImmediateResultDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                httpEntity,
                SciDirImmediateResultDto.class);

        return responseEntity.getBody();
    }

    public SciDirImmediateResultDto extractInfoFromScienceDirectWithRetries(ElsevierScienceDirectConsumerDto elsevierScienceDirectConsumerDto, String transactionId) {

        return Failsafe
                .with(basicRetryPolicyForConnectors)
                .withFallback(() -> {
                    throw new RepositorerBusinessException(RepositorerError.CONNECTOR_CONNECTION_ERROR,
                            RepositorerError.CONNECTOR_CONNECTION_ERROR.getMessage(),
                            this.getClass().getName());
                })
                .onSuccess(s -> log.info("ScienceDirectConsumerConnector#extractInfoFromScienceDirectWithRetries, completed successfully!"))
                .onFailure(error -> log.error("ScienceDirectConsumerConnector#extractInfoFromScienceDirectWithRetries, failed!"))
                .onAbort(error -> log.error("ScienceDirectConsumerConnector#extractInfoFromScienceDirectWithRetries, aborted!"))
                .get((context) -> {

                    long startTime = context.getStartTime().toMillis();
                    long elapsedTime = context.getElapsedTime().toMillis();
                    int executions = context.getExecutions();

                    String message = String.format("ScienceDirectConsumerConnector#extractInfoFromScienceDirectWithRetries, retrying...with params: " +
                            "[executions=%s, startTime(ms)=%s, elapsedTime(ms)=%s, elsevierScienceDirectConsumerDto=%s, transactionId=%s]", executions, startTime, elapsedTime, elsevierScienceDirectConsumerDto, transactionId);

                    log.warn(message);

                    return extractInfoFromScienceDirect(elsevierScienceDirectConsumerDto, transactionId);
                });
    }
}
