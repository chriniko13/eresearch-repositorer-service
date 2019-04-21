package com.eresearch.repositorer.connector;

import com.eresearch.repositorer.dto.authormatcher.request.AuthorComparisonDto;
import com.eresearch.repositorer.dto.authormatcher.response.AuthorMatcherResultsDto;
import com.eresearch.repositorer.exception.business.RepositorerBusinessException;
import com.eresearch.repositorer.exception.error.RepositorerError;
import lombok.extern.log4j.Log4j;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Log4j
@Component
public class AuthorMatcherConnector implements Connector {

    @Value("${author.matcher.url}")
    private String authorMatcherUrl;

    @Autowired
    @Qualifier("repositorerRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    @Qualifier("basicRetryPolicyForConnectors")
    private RetryPolicy basicRetryPolicyForConnectors;

    private AuthorMatcherResultsDto performAuthorMatching(AuthorComparisonDto authorComparisonDto) {

        final String url = authorMatcherUrl;

        final RequestEntity<AuthorComparisonDto> requestEntity = new RequestEntity<>(
                authorComparisonDto,
                HttpMethod.POST,
                URI.create(url));

        final ResponseEntity<AuthorMatcherResultsDto> responseEntity = restTemplate.exchange(
                requestEntity,
                AuthorMatcherResultsDto.class);

        return responseEntity.getBody();
    }

    public AuthorMatcherResultsDto performAuthorMatchingWithRetries(AuthorComparisonDto authorComparisonDto) {
        return Failsafe
                .with(basicRetryPolicyForConnectors)
                .withFallback(() -> {
                    throw new RepositorerBusinessException(RepositorerError.CONNECTOR_CONNECTION_ERROR,
                            RepositorerError.CONNECTOR_CONNECTION_ERROR.getMessage(),
                            this.getClass().getName());
                })
                .onSuccess(s -> log.info("AuthorMatcherConnector#performAuthorMatchingWithRetries, completed successfully!"))
                .onFailure(error -> log.error("AuthorMatcherConnector#performAuthorMatchingWithRetries, failed!"))
                .onAbort(error -> log.error("AuthorMatcherConnector#performAuthorMatchingWithRetries, aborted!"))
                .get((context) -> {

                    long startTime = context.getStartTime().toMillis();
                    long elapsedTime = context.getElapsedTime().toMillis();
                    int executions = context.getExecutions();

                    String message = String.format("AuthorMatcherConnector#performAuthorMatchingWithRetries, retrying...with params: " +
                            "[executions=%s, startTime(ms)=%s, elapsedTime(ms)=%s, authorComparisonDto=%s]", executions, startTime, elapsedTime, authorComparisonDto);

                    log.warn(message);

                    return performAuthorMatching(authorComparisonDto);
                });
    }
}
