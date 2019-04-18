package com.eresearch.repositorer.activator.externalsystem;

import com.eresearch.repositorer.connector.ElsevierAuthorConsumerConnector;
import com.eresearch.repositorer.dto.elsevierauthor.request.AuthorFinderDto;
import com.eresearch.repositorer.dto.elsevierauthor.request.AuthorNameDto;
import com.eresearch.repositorer.dto.elsevierauthor.response.AuthorFinderImmediateResultDto;
import com.eresearch.repositorer.dto.repositorer.request.RepositorerFindDto;
import com.eresearch.repositorer.exception.business.RepositorerBusinessException;
import com.eresearch.repositorer.transformer.dto.RepositorerFindDtos;
import lombok.extern.log4j.Log4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;


@Log4j
@Component
public class ElsevierAuthorConsumptionActivator implements ConsumptionActivator {

    @Autowired
    private ElsevierAuthorConsumerConnector authorConsumerConnector;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void send(Message<?> message) {
        try {
            log.info("ElsevierAuthorConsumptionActivator --> " + Thread.currentThread().getName());

            final RepositorerFindDtos repositorerFindDtos = (RepositorerFindDtos) message.getPayload();
            final String transactionId = (String) message.getHeaders().get(TRANSACTION_ID);

            for (RepositorerFindDto repositorerFindDto : repositorerFindDtos.getDtos()) {

                final AuthorNameDto authorNameDto = modelMapper.map(repositorerFindDto, AuthorNameDto.class);
                final AuthorFinderDto authorFinderDto = AuthorFinderDto.builder().authorName(authorNameDto).build();


                final AuthorFinderImmediateResultDto immediateResult
                        = authorConsumerConnector.extractInfoFromElsevierAuthorWithRetries(authorFinderDto, transactionId);

                log.info("ElsevierAuthorConsumptionActivator#send --- immediateResult = " + immediateResult);


            }
        } catch (RepositorerBusinessException e) {

            log.error("ElsevierAuthorConsumptionActivator#send --- error occurred.", e);
            throw e;
        }
    }
}
