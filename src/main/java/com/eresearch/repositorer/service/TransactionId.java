package com.eresearch.repositorer.service;

import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

@Component
@RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TransactionId {

    private final String transactionId;

    public TransactionId() {
        transactionId = UUID.randomUUID().toString();
    }

    public String getTransactionId() {
        return transactionId;
    }
}
