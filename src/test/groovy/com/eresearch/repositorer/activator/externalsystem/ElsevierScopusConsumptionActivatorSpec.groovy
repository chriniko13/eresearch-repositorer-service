package com.eresearch.repositorer.activator.externalsystem

import com.eresearch.repositorer.connector.ElsevierScopusConsumerConnector
import com.eresearch.repositorer.dto.scopus.request.ElsevierScopusConsumerDto
import com.eresearch.repositorer.exception.business.RepositorerBusinessException
import com.eresearch.repositorer.exception.error.RepositorerError
import com.eresearch.repositorer.transformer.dto.ElsevierAuthorResultsTransformerDto
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import spock.lang.Specification

class ElsevierScopusConsumptionActivatorSpec extends Specification {

    ElsevierScopusConsumptionActivator elsevierScopusConsumptionActivator
    ElsevierScopusConsumerConnector elsevierScopusConsumerConnector


    def setup() {
        elsevierScopusConsumerConnector = Mock(ElsevierScopusConsumerConnector)

        elsevierScopusConsumptionActivator = new ElsevierScopusConsumptionActivator(
                scopusConsumerConnector: elsevierScopusConsumerConnector)
    }

    def "send method works as expected"() {

        given:
            String transactionIdHeader = "Transaction-Id";
            String txId = UUID.randomUUID()

            ElsevierAuthorResultsTransformerDto elsevierAuthorResultsTransformerDto = new ElsevierAuthorResultsTransformerDto(["111", "222"])

            Message<?> message = MessageBuilder.withPayload(elsevierAuthorResultsTransformerDto).setHeader(transactionIdHeader, txId).build()

        when:
            elsevierScopusConsumptionActivator.send(message)

        then:
            2 * elsevierScopusConsumerConnector.extractInfoFromScopusWithRetries(_ as ElsevierScopusConsumerDto, txId)
            0 * _

    }

    def "send method works as expected - error occurred"() {

        given:
            String transactionIdHeader = "Transaction-Id";
            String txId = UUID.randomUUID()

            ElsevierAuthorResultsTransformerDto elsevierAuthorResultsTransformerDto = new ElsevierAuthorResultsTransformerDto(["111", "222"])

            Message<?> message = MessageBuilder.withPayload(elsevierAuthorResultsTransformerDto).setHeader(transactionIdHeader, txId).build()

        when:
            elsevierScopusConsumptionActivator.send(message)

        then:
            1 * elsevierScopusConsumerConnector.extractInfoFromScopusWithRetries(_ as ElsevierScopusConsumerDto, txId) >> {
                throw new RepositorerBusinessException(RepositorerError.CONNECTOR_CONNECTION_ERROR,
                        RepositorerError.CONNECTOR_CONNECTION_ERROR.getMessage(),
                        ElsevierScopusConsumerConnector.name)
            }
            0 * _

        and:
            RepositorerBusinessException ex = thrown()
            ex.repositorerError == RepositorerError.CONNECTOR_CONNECTION_ERROR
            ex.message == RepositorerError.CONNECTOR_CONNECTION_ERROR.message
            ex.crashedComponentName == ElsevierScopusConsumerConnector.name

    }

}
