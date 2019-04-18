package com.eresearch.repositorer.activator.externalsystem;

import com.eresearch.repositorer.connector.ElsevierScopusConsumerConnector;
import com.eresearch.repositorer.dto.scopus.request.ElsevierScopusConsumerDto;
import com.eresearch.repositorer.dto.scopus.response.ScopusFinderImmediateResultDto;
import com.eresearch.repositorer.exception.business.RepositorerBusinessException;
import com.eresearch.repositorer.transformer.dto.ElsevierAuthorResultsTransformerDto;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;


@Log4j
@Component
public class ElsevierScopusConsumptionActivator implements ConsumptionActivator {

    @Autowired
    private ElsevierScopusConsumerConnector scopusConsumerConnector;

    @Override
    public void send(Message<?> message) {
        try {
            log.info("ElsevierScopusConsumptionActivator --> " + Thread.currentThread().getName());

            final ElsevierAuthorResultsTransformerDto elsevierAuthorResultsTransformerDto = (ElsevierAuthorResultsTransformerDto) message.getPayload();
            final String transactionId = (String) message.getHeaders().get(TRANSACTION_ID);

            for (String elsevierAuthorId : elsevierAuthorResultsTransformerDto.getElsevierAuthorIds()) {


                ScopusFinderImmediateResultDto immediateResult
                        = scopusConsumerConnector.extractInfoFromScopusWithRetries(new ElsevierScopusConsumerDto(elsevierAuthorId), transactionId);

                log.info("ElsevierScopusConsumptionActivator#send --- immediateResult = " + immediateResult);
            }
        } catch (RepositorerBusinessException e) {

            log.error("ElsevierScopusConsumptionActivator#send --- error occurred.", e);
            throw e;
        }
    }
}
