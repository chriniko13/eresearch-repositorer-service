package com.eresearch.repositorer.activator.externalsystem;

import com.eresearch.repositorer.connector.ScienceDirectConsumerConnector;
import com.eresearch.repositorer.dto.repositorer.request.RepositorerFindDto;
import com.eresearch.repositorer.dto.sciencedirect.request.ElsevierScienceDirectConsumerDto;
import com.eresearch.repositorer.dto.sciencedirect.response.SciDirImmediateResultDto;
import com.eresearch.repositorer.exception.business.RepositorerBusinessException;
import com.eresearch.repositorer.transformer.dto.RepositorerFindDtos;
import lombok.extern.log4j.Log4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Log4j
@Component
public class ScienceDirectConsumptionActivator implements ConsumptionActivator {

    @Autowired
    private ScienceDirectConsumerConnector scienceDirectConsumerConnector;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void send(final Message<?> message) {
        try {
            log.info("ScienceDirectConsumptionActivator --> " + Thread.currentThread().getName());

            final RepositorerFindDtos repositorerFindDtos = (RepositorerFindDtos) message.getPayload();
            final String transactionId = (String) message.getHeaders().get(TRANSACTION_ID);

            for (RepositorerFindDto repositorerFindDto : repositorerFindDtos.getDtos()) {

                final ElsevierScienceDirectConsumerDto scienceDirectConsumerDto
                        = modelMapper.map(repositorerFindDto, ElsevierScienceDirectConsumerDto.class);


                final SciDirImmediateResultDto immediateResult
                        = scienceDirectConsumerConnector.extractInfoFromScienceDirectWithRetries(scienceDirectConsumerDto, transactionId);

                log.info("ScienceDirectConsumptionEventSender#send --- immediateResult = " + immediateResult);


            }
        } catch (RepositorerBusinessException e) {

            log.error("ScienceDirectConsumptionEventSender#send --- error occurred.", e);
            throw e;
        }
    }
}
