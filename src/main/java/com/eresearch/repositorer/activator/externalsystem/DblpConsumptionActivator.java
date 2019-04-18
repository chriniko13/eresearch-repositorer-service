package com.eresearch.repositorer.activator.externalsystem;

import com.eresearch.repositorer.connector.DblpConsumerConnector;
import com.eresearch.repositorer.dto.dblp.request.DblpConsumerDto;
import com.eresearch.repositorer.dto.dblp.response.DblpImmediateResultDto;
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
public class DblpConsumptionActivator implements ConsumptionActivator {

    @Autowired
    private DblpConsumerConnector dblpConsumerConnector;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void send(final Message<?> message) {

        try {
            log.info("DblpConsumptionActivator --> " + Thread.currentThread().getName());

            final RepositorerFindDtos repositorerFindDtos
                    = (RepositorerFindDtos) message.getPayload();

            final String transactionId = (String) message.getHeaders().get(TRANSACTION_ID);

            for (RepositorerFindDto repositorerFindDto : repositorerFindDtos.getDtos()) {

                final DblpConsumerDto dblpConsumerDto = modelMapper.map(repositorerFindDto, DblpConsumerDto.class);


                final DblpImmediateResultDto immediateResult
                        = dblpConsumerConnector.extractInfoFromDblpWithRetries(dblpConsumerDto, transactionId);

                log.info("DblpConsumptionEventSender#send --- immediateResult = " + immediateResult);
            }

        } catch (RepositorerBusinessException e) {

            log.error("DblpConsumptionEventSender#send --- error occurred.", e);

            throw e;
        }
    }
}
