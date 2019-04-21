package com.eresearch.repositorer.connector;

import com.eresearch.repositorer.dto.elsevierauthor.request.AuthorFinderDto;
import com.eresearch.repositorer.dto.elsevierauthor.response.AuthorFinderImmediateResultDto;
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
public class ElsevierAuthorConsumerConnector implements Connector {

    private static final String TRANSACTION_ID = "Transaction-Id";

    @Value("${elsevier.author.consumer.url}")
    private String elsevierAuthorConsumerUrl;

    @Autowired
    @Qualifier("repositorerRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    @Qualifier("basicRetryPolicyForConnectors")
    private RetryPolicy basicRetryPolicyForConnectors;

    private AuthorFinderImmediateResultDto extractInfoFromElsevierAuthor(AuthorFinderDto authorFinderDto, String transactionId) {

        final String url = elsevierAuthorConsumerUrl;

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(TRANSACTION_ID, transactionId);

        final HttpEntity<AuthorFinderDto> httpEntity
                = new HttpEntity<>(authorFinderDto, httpHeaders);

        final ResponseEntity<AuthorFinderImmediateResultDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                httpEntity,
                AuthorFinderImmediateResultDto.class);

        return responseEntity.getBody();
    }

    public AuthorFinderImmediateResultDto extractInfoFromElsevierAuthorWithRetries(AuthorFinderDto authorFinderDto, String transactionId) {

        return Failsafe
                .with(basicRetryPolicyForConnectors)
                .withFallback(() -> {
                    throw new RepositorerBusinessException(RepositorerError.CONNECTOR_CONNECTION_ERROR,
                            RepositorerError.CONNECTOR_CONNECTION_ERROR.getMessage(),
                            this.getClass().getName());
                })
                .onSuccess(s -> log.info("ElsevierAuthorConsumerConnector#extractInfoFromElsevierAuthorWithRetries, completed successfully!"))
                .onFailure(error -> log.error("ElsevierAuthorConsumerConnector#extractInfoFromElsevierAuthorWithRetries, failed!"))
                .onAbort(error -> log.error("ElsevierAuthorConsumerConnector#extractInfoFromElsevierAuthorWithRetries, aborted!"))
                .get((context) -> {

                    long startTime = context.getStartTime().toMillis();
                    long elapsedTime = context.getElapsedTime().toMillis();
                    int executions = context.getExecutions();

                    String message = String.format("ElsevierAuthorConsumerConnector#extractInfoFromElsevierAuthorWithRetries, retrying...with params: " +
                            "[executions=%s, startTime(ms)=%s, elapsedTime(ms)=%s, authorFinderDto=%s, transactionId=%s]", executions, startTime, elapsedTime, authorFinderDto, transactionId);

                    log.warn(message);

                    return extractInfoFromElsevierAuthor(authorFinderDto, transactionId);
                });

    }
}
