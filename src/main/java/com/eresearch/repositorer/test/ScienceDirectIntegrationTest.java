package com.eresearch.repositorer.test;

import com.eresearch.repositorer.connector.ScienceDirectConsumerConnector;
import com.eresearch.repositorer.dto.sciencedirect.request.ElsevierScienceDirectConsumerDto;
import com.eresearch.repositorer.dto.sciencedirect.response.SciDirImmediateResultDto;
import com.eresearch.repositorer.exception.business.RepositorerBusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ScienceDirectIntegrationTest {

    @Autowired
    private ScienceDirectConsumerConnector scienceDirectConsumerConnector;

    @Autowired
    private ObjectMapper objectMapper;

    public void testScienceDirectIntegration() throws RepositorerBusinessException, JsonProcessingException {

        ElsevierScienceDirectConsumerDto elsevierScienceDirectConsumerDto
                = ElsevierScienceDirectConsumerDto
                .builder()
                .firstname("A.")
                .surname("Tsolakidis")
                .build();

        SciDirImmediateResultDto sciDirImmediateResultDto = scienceDirectConsumerConnector.extractInfoFromScienceDirectWithRetries(elsevierScienceDirectConsumerDto,
                UUID.randomUUID().toString());

        String resultAsAString = objectMapper.writeValueAsString(sciDirImmediateResultDto);
        System.out.println("[SCIDIR INTEGRATION RESULT] == " + resultAsAString);

    }
}
