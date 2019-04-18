package com.eresearch.repositorer.activator.externalsystem

import com.eresearch.repositorer.connector.ScienceDirectConsumerConnector
import com.eresearch.repositorer.dto.repositorer.request.RepositorerFindDto
import com.eresearch.repositorer.dto.sciencedirect.request.ElsevierScienceDirectConsumerDto
import com.eresearch.repositorer.exception.business.RepositorerBusinessException
import com.eresearch.repositorer.exception.error.RepositorerError
import com.eresearch.repositorer.transformer.dto.RepositorerFindDtos
import org.modelmapper.ModelMapper
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import spock.lang.Specification

class ScienceDirectConsumptionActivatorSpec extends Specification {

    ScienceDirectConsumptionActivator scienceDirectConsumptionActivator
    ScienceDirectConsumerConnector scienceDirectConsumerConnector
    ModelMapper modelMapper

    def setup() {

        scienceDirectConsumerConnector = Mock(ScienceDirectConsumerConnector)
        modelMapper = Mock(ModelMapper)

        scienceDirectConsumptionActivator = new ScienceDirectConsumptionActivator(
                scienceDirectConsumerConnector: scienceDirectConsumerConnector,
                modelMapper: modelMapper
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
            scienceDirectConsumptionActivator.send(message)


        then:
            2 * modelMapper.map(_ as RepositorerFindDto, ElsevierScienceDirectConsumerDto) >> new ElsevierScienceDirectConsumerDto()
            2 * scienceDirectConsumerConnector.extractInfoFromScienceDirectWithRetries(_ as ElsevierScienceDirectConsumerDto, txId)
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
            scienceDirectConsumptionActivator.send(message)


        then:
            1 * modelMapper.map(_ as RepositorerFindDto, ElsevierScienceDirectConsumerDto) >> new ElsevierScienceDirectConsumerDto()
            1 * scienceDirectConsumerConnector.extractInfoFromScienceDirectWithRetries(_ as ElsevierScienceDirectConsumerDto, txId) >> {
                throw new RepositorerBusinessException(RepositorerError.CONNECTOR_CONNECTION_ERROR,
                        RepositorerError.CONNECTOR_CONNECTION_ERROR.getMessage(),
                        ScienceDirectConsumerConnector.name)
            }
            0 * _

        and:
            RepositorerBusinessException ex = thrown()
            ex.repositorerError == RepositorerError.CONNECTOR_CONNECTION_ERROR
            ex.message == RepositorerError.CONNECTOR_CONNECTION_ERROR.message
            ex.crashedComponentName == ScienceDirectConsumerConnector.name

    }
}
