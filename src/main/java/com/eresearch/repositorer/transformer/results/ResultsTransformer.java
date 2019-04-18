package com.eresearch.repositorer.transformer.results;

import org.springframework.messaging.Message;

public interface ResultsTransformer {

    String TRANSACTION_ID = "Transaction-Id";

    Message<?> transform(Message<?> message);
}
