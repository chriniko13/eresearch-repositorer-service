package com.eresearch.repositorer.aggregator.correlation;

import org.springframework.integration.aggregator.CorrelationStrategy;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class TransactionIdCorrelationStrategy implements CorrelationStrategy {

    private static final String TRANSACTION_ID = "Transaction-Id";

    @Override
    public Object getCorrelationKey(Message<?> message) {
        return message.getHeaders().get(TRANSACTION_ID);
    }
}
