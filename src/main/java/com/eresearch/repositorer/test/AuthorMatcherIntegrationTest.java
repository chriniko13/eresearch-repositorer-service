package com.eresearch.repositorer.test;

import com.eresearch.repositorer.connector.AuthorMatcherConnector;
import com.eresearch.repositorer.dto.authormatcher.request.AuthorComparisonDto;
import com.eresearch.repositorer.dto.authormatcher.request.AuthorNameDto;
import com.eresearch.repositorer.dto.authormatcher.response.AuthorMatcherResultsDto;
import com.eresearch.repositorer.exception.business.RepositorerBusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthorMatcherIntegrationTest {

    @Autowired
    private AuthorMatcherConnector authorMatcherConnector;

    @Autowired
    private ObjectMapper objectMapper;

    public void testIntegrationWithAuthorMatcher() throws RepositorerBusinessException, JsonProcessingException {

        AuthorComparisonDto authorComparisonDto = AuthorComparisonDto.builder()
                .firstAuthorName(AuthorNameDto.builder()
                        .firstName("Nikolaos")
                        .surname("Christidis")
                        .build())
                .secondAuthorName(AuthorNameDto.builder()
                        .firstName("N.")
                        .surname("Christidis")
                        .build())
                .build();

        AuthorMatcherResultsDto authorMatcherResultsDto = authorMatcherConnector.performAuthorMatchingWithRetries(authorComparisonDto);

        String resultAsAString = objectMapper.writeValueAsString(authorMatcherResultsDto);
        System.out.println("[AUTHOR MATCHER INTEGRATION RESULT] == " + resultAsAString);

    }
}
