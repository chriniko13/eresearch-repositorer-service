package com.eresearch.repositorer.aggregator.release

import org.springframework.integration.store.SimpleMessageGroup
import org.springframework.integration.support.MessageBuilder
import org.springframework.messaging.Message
import spock.lang.Specification
import spock.lang.Unroll

class ElsevierAuthorMessagesAwaitingReleaseStrategySpec extends Specification {

    ElsevierAuthorMessagesAwaitingReleaseStrategy releaseStrategy

    def setup() {

        releaseStrategy = new ElsevierAuthorMessagesAwaitingReleaseStrategy()
    }

    @Unroll("noOfMessages = #noOfMessages, outcome = #outcome")
    def "CanRelease method works as expected"() {

        given:
            Collection<Message<?>> messages = (1..noOfMessages).collect { idx -> MessageBuilder.withPayload("$idx").build()}
            SimpleMessageGroup messageGroup = new SimpleMessageGroup(messages, "group-id")

        when:
            boolean result = releaseStrategy.canRelease(messageGroup)

        then:
            result == outcome

        and:
            0 * _
            noExceptionThrown()

        where:
            noOfMessages    |   outcome
                3           |   true
                2           |   false
    }
}
