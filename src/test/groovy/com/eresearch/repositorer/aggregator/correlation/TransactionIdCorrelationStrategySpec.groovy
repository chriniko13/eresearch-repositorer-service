package com.eresearch.repositorer.aggregator.correlation

import org.springframework.integration.support.MessageBuilder
import org.springframework.messaging.Message
import spock.lang.Specification

class TransactionIdCorrelationStrategySpec extends Specification {

    TransactionIdCorrelationStrategy transactionIdCorrelationStrategy

    def setup() {
        transactionIdCorrelationStrategy = new TransactionIdCorrelationStrategy()
    }

    def "GetCorrelationKey method works as expected"() {

        given:
            String txId = UUID.randomUUID()
            Message<?> message = MessageBuilder.withPayload("some-payload-here").setHeader("Transaction-Id", txId).build()

        when:
            Object result = transactionIdCorrelationStrategy.getCorrelationKey(message)

        then:
            result == txId
            noExceptionThrown()

        and:
            0 * _

    }
}
