package com.eresearch.repositorer.activator

import com.eresearch.repositorer.domain.common.NameVariant
import com.eresearch.repositorer.domain.lookup.NameLookup
import com.eresearch.repositorer.domain.lookup.NameLookupStatus
import com.eresearch.repositorer.dto.repositorer.request.RepositorerFindDto
import com.eresearch.repositorer.repository.NamesLookupRepository
import com.eresearch.repositorer.transformer.dto.RepositorerFindDtos
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import spock.lang.Specification

import java.time.Clock
import java.time.Instant

class NamesLookupStorageActivatorSpec extends Specification {

    NamesLookupRepository mockedNamesLookupRepository
    Clock clock
    NamesLookupStorageActivator namesLookupStorageActivator

    def setup() {

        mockedNamesLookupRepository = Mock(NamesLookupRepository)
        clock = Clock.systemUTC()

        namesLookupStorageActivator = new NamesLookupStorageActivator(namesLookupRepository: mockedNamesLookupRepository, clock: clock)
    }


    def "store method works as expected"() {

        given: 'have a message with payload and headers'
            String transactionIdHeader = "Transaction-Id";
            String txId = UUID.randomUUID()

            RepositorerFindDto repositorerFindDto1 = new RepositorerFindDto(firstname: "firstname1", initials: "initials1", surname: "surname1")
            RepositorerFindDto repositorerFindDto2 = new RepositorerFindDto(firstname: "firstname2", initials: "initials2", surname: "surname2")

            RepositorerFindDtos repositorerFindDtos = new RepositorerFindDtos(dtos: [repositorerFindDto1, repositorerFindDto2])

            Message<?> message = MessageBuilder.withPayload(repositorerFindDtos).setHeader(transactionIdHeader, txId).build()

            Collection<NameVariant> nameVariants = [new NameVariant(firstname: repositorerFindDto2.firstname, initials: repositorerFindDto2.initials, surname:  repositorerFindDto2.surname)]

            NameLookup nameLookup = new NameLookup(txId, repositorerFindDto1.firstname, repositorerFindDto1.initials, repositorerFindDto1.surname,
                    nameVariants, NameLookupStatus.PENDING, Instant.now(clock))

        when: 'nameLookupStorageActivator store method is called'
            Message<?> result = namesLookupStorageActivator.store(message)

        then: 'we have all correct interactions'
            1 * mockedNamesLookupRepository.save(nameLookup)
            0 * _

        and: 'with the correct output and no exception is thrown'
            result.headers[transactionIdHeader] == message.headers[transactionIdHeader]
            result.payload == message.payload

            noExceptionThrown()
    }
}
