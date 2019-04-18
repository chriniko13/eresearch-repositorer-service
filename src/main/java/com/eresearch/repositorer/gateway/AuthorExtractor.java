package com.eresearch.repositorer.gateway;

import com.eresearch.repositorer.dto.repositorer.request.RepositorerFindDto;
import org.springframework.integration.annotation.Gateway;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.concurrent.Future;

public interface AuthorExtractor {

    @Gateway
    Future<Void> extract(@Payload RepositorerFindDto repositorerFindDto,
                         @Header("Transaction-Id") String transactionId);
}
