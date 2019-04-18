package com.eresearch.repositorer.test;

import com.eresearch.repositorer.connector.ElsevierAuthorConsumerConnector;
import com.eresearch.repositorer.dto.elsevierauthor.request.AuthorFinderDto;
import com.eresearch.repositorer.dto.elsevierauthor.request.AuthorNameDto;
import com.eresearch.repositorer.dto.elsevierauthor.response.AuthorFinderImmediateResultDto;
import com.eresearch.repositorer.dto.repositorer.request.RepositorerFindDto;
import com.eresearch.repositorer.exception.business.RepositorerBusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ElsevierAuthorIntegrationTest {

    @Autowired
    private ElsevierAuthorConsumerConnector connector;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelMapper modelMapper;

    public void testIntegrationWithElsevierAuthor() throws RepositorerBusinessException, JsonProcessingException {

        //we will test also how modelmapper behaves here...

        RepositorerFindDto repositorerFindDto = RepositorerFindDto.builder()
                .firstname("Anastasios")
                .surname("Tsolakidis")
                .build();

        AuthorNameDto authorNameDto = modelMapper.map(repositorerFindDto, AuthorNameDto.class);
        AuthorFinderDto authorFinderDto = new AuthorFinderDto(authorNameDto);

        AuthorFinderImmediateResultDto authorImmediateResult =
                connector.extractInfoFromElsevierAuthorWithRetries(authorFinderDto, UUID.randomUUID().toString());

        String resultAsAString = objectMapper.writeValueAsString(authorImmediateResult);
        System.out.println("[ELSEVIER AUTHOR INTEGRATION RESULT] == " + resultAsAString);
    }
}
