package com.eresearch.repositorer.test;

import com.eresearch.repositorer.connector.DblpConsumerConnector;
import com.eresearch.repositorer.dto.dblp.request.DblpConsumerDto;
import com.eresearch.repositorer.dto.dblp.response.DblpImmediateResultDto;
import com.eresearch.repositorer.exception.business.RepositorerBusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DblpConsumerIntegrationTest {

    @Autowired
    private DblpConsumerConnector dblpConsumerConnector;

    @Autowired
    private ObjectMapper objectMapper;

    public void testDblpConsumerIntegration() throws RepositorerBusinessException, JsonProcessingException {

        DblpConsumerDto dblpConsumerDto = DblpConsumerDto.builder()
                .firstname("Anastasios")
                .surname("Tsolakidis")
                .build();

        DblpImmediateResultDto dblpImmediateResultDto =
                dblpConsumerConnector.extractInfoFromDblpWithRetries(dblpConsumerDto, UUID.randomUUID().toString());

        String resultAsAString = objectMapper.writeValueAsString(dblpImmediateResultDto);
        System.out.println("[DBLP INTEGRATION RESULT] == " + resultAsAString);

    }

}
