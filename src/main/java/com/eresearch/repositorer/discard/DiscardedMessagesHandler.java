package com.eresearch.repositorer.discard;

import com.eresearch.repositorer.domain.discard.DiscardedMessage;
import com.eresearch.repositorer.repository.DiscardedMessageRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Log4j
@Component
public class DiscardedMessagesHandler {

    @Autowired
    private Clock clock;

    @Autowired
    private DiscardedMessageRepository discardedMessageRepository;

    public void handleDiscardedMessage(Message<?> message) {
        log.info("DiscardedMessagesHandler#handleDiscardedMessage --- fired!");
        discardedMessageRepository.store(new DiscardedMessage(UUID.randomUUID().toString(), Instant.now(clock), message.toString()));
    }
}
