package com.eresearch.repositorer.activator.externalsystem

import com.eresearch.repositorer.connector.ElsevierAuthorConsumerConnector
import com.eresearch.repositorer.dto.elsevierauthor.request.AuthorFinderDto
import com.eresearch.repositorer.dto.elsevierauthor.request.AuthorNameDto
import com.eresearch.repositorer.dto.repositorer.request.RepositorerFindDto
import com.eresearch.repositorer.exception.business.RepositorerBusinessException
import com.eresearch.repositorer.exception.error.RepositorerError
import com.eresearch.repositorer.transformer.dto.RepositorerFindDtos
import org.modelmapper.ModelMapper
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import spock.lang.Specification

class ElsevierAuthorConsumptionActivatorSpec extends Specification {

    ElsevierAuthorConsumptionActivator elsevierAuthorConsumptionActivator

    ModelMapper modelMapper
    ElsevierAuthorConsumerConnector elsevierAuthorConsumerConnector

    def setup() {
        modelMapper = Mock(ModelMapper)
        elsevierAuthorConsumerConnector = Mock(ElsevierAuthorConsumerConnector)

        elsevierAuthorConsumptionActivator = new ElsevierAuthorConsumptionActivator(
                modelMapper: modelMapper,
                authorConsumerConnector: elsevierAuthorConsumerConnector
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
            elsevierAuthorConsumptionActivator.send(message)

        then:
            2 * modelMapper.map(_ as RepositorerFindDto, AuthorNameDto) >> new AuthorNameDto()
            2 * elsevierAuthorConsumerConnector.extractInfoFromElsevierAuthorWithRetries(_ as AuthorFinderDto, txId)
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
            elsevierAuthorConsumptionActivator.send(message)

        then:
            1 * modelMapper.map(_ as RepositorerFindDto, AuthorNameDto) >> new AuthorNameDto()
            1 * elsevierAuthorConsumerConnector.extractInfoFromElsevierAuthorWithRetries(_ as AuthorFinderDto, txId) >> {
                throw new RepositorerBusinessException(RepositorerError.CONNECTOR_CONNECTION_ERROR,
                        RepositorerError.CONNECTOR_CONNECTION_ERROR.getMessage(),
                        ElsevierAuthorConsumerConnector.name)
            }
            0 * _

        and:
            RepositorerBusinessException ex = thrown()
            ex.repositorerError == RepositorerError.CONNECTOR_CONNECTION_ERROR
            ex.message == RepositorerError.CONNECTOR_CONNECTION_ERROR.message
            ex.crashedComponentName == ElsevierAuthorConsumerConnector.name

    }
}
