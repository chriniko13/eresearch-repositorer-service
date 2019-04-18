package com.eresearch.repositorer.activator.externalsystem

import com.eresearch.repositorer.connector.DblpConsumerConnector
import com.eresearch.repositorer.dto.dblp.request.DblpConsumerDto
import com.eresearch.repositorer.dto.repositorer.request.RepositorerFindDto
import com.eresearch.repositorer.exception.business.RepositorerBusinessException
import com.eresearch.repositorer.exception.error.RepositorerError
import com.eresearch.repositorer.transformer.dto.RepositorerFindDtos
import org.modelmapper.ModelMapper
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import spock.lang.Specification

class DblpConsumptionActivatorSpec extends Specification {

    DblpConsumptionActivator dblpConsumptionActivator

    DblpConsumerConnector mockedDblpConsumerConnector
    ModelMapper mockedModelMapper

    def setup() {

        mockedDblpConsumerConnector = Mock(DblpConsumerConnector)
        mockedModelMapper = Mock(ModelMapper)

        dblpConsumptionActivator = new DblpConsumptionActivator(
                dblpConsumerConnector: mockedDblpConsumerConnector,
                modelMapper: mockedModelMapper
        )
    }


    def "send method works as expected"() {

        given:
            String transactionIdHeader = "Transaction-Id";
            String txId = UUID.randomUUID()

            RepositorerFindDto repositorerFindDto1 = new RepositorerFindDto(firstname: "firstname1", initials: "initials1", surname: "surname1")
            RepositorerFindDto repositorerFindDto2 = new RepositorerFindDto(firstname: "firstname2", initials: "initials2", surname: "surname2")

            RepositorerFindDtos repositorerFindDtos = new RepositorerFindDtos(dtos: [repositorerFindDto1, repositorerFindDto2])

            Message<?> message = MessageBuilder.withPayload(repositorerFindDtos).setHeader(transactionIdHeader, txId).build()

        when:
            dblpConsumptionActivator.send(message)

        then:
            2 * mockedModelMapper.map(_ as RepositorerFindDto, DblpConsumerDto) >> new DblpConsumerDto()
            2 * mockedDblpConsumerConnector.extractInfoFromDblpWithRetries(_ as DblpConsumerDto, txId)
            0 * _

        and:
            noExceptionThrown()

    }


    def "send method works as expected - error occurred"() {

        given:
            String transactionIdHeader = "Transaction-Id";
            String txId = UUID.randomUUID()

            RepositorerFindDto repositorerFindDto1 = new RepositorerFindDto(firstname: "firstname1", initials: "initials1", surname: "surname1")
            RepositorerFindDto repositorerFindDto2 = new RepositorerFindDto(firstname: "firstname2", initials: "initials2", surname: "surname2")

            RepositorerFindDtos repositorerFindDtos = new RepositorerFindDtos(dtos: [repositorerFindDto1, repositorerFindDto2])

            Message<?> message = MessageBuilder.withPayload(repositorerFindDtos).setHeader(transactionIdHeader, txId).build()

        when:
            dblpConsumptionActivator.send(message)

        then:
            1 * mockedModelMapper.map(_ as RepositorerFindDto, DblpConsumerDto) >> new DblpConsumerDto()
            1 * mockedDblpConsumerConnector.extractInfoFromDblpWithRetries(_ as DblpConsumerDto, txId) >> {
                throw new RepositorerBusinessException(RepositorerError.CONNECTOR_CONNECTION_ERROR,
                        RepositorerError.CONNECTOR_CONNECTION_ERROR.getMessage(),
                        DblpConsumerConnector.name)
            }
            0 * _

        and:
            RepositorerBusinessException ex = thrown()
            ex.repositorerError == RepositorerError.CONNECTOR_CONNECTION_ERROR
            ex.message == RepositorerError.CONNECTOR_CONNECTION_ERROR.message
            ex.crashedComponentName == DblpConsumerConnector.name

    }
}
