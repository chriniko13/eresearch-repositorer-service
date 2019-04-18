package com.eresearch.repositorer.activator.externalsystem;

import org.springframework.messaging.Message;

/**
 * This is used as a marker interface.
 */
public interface ConsumptionActivator {

    String TRANSACTION_ID = "Transaction-Id";

    void send(final Message<?> message);
}
