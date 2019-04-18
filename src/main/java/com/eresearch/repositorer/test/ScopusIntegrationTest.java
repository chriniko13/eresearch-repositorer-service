package com.eresearch.repositorer.test;

import com.eresearch.repositorer.connector.ElsevierScopusConsumerConnector;
import com.eresearch.repositorer.dto.scopus.request.ElsevierScopusConsumerDto;
import com.eresearch.repositorer.dto.scopus.response.ScopusFinderImmediateResultDto;
import com.eresearch.repositorer.exception.business.RepositorerBusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ScopusIntegrationTest {

    @Autowired
    private ElsevierScopusConsumerConnector connector;

    @Autowired
    private ObjectMapper objectMapper;

    public void testScopusIntegration() throws RepositorerBusinessException, JsonProcessingException {

        ScopusFinderImmediateResultDto scopusImmediateResult = connector.extractInfoFromScopusWithRetries(
                ElsevierScopusConsumerDto.builder().scopusAuthorIdentifierNumber("23007591800").build(),
                UUID.randomUUID().toString());

        String resultAsAString = objectMapper.writeValueAsString(scopusImmediateResult);
        System.out.println("[SCOPUS INTEGRATION RESULT] == " + resultAsAString);
    }

}
