package com.eresearch.repositorer.error.resolver;

import com.eresearch.repositorer.error.handler.ErrorHandler;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import java.util.List;


@Log4j
@Component
public class ErrorResolver {

    @Autowired
    private List<ErrorHandler> errorHandlers;

    public void handleFailedMessage(Message<MessagingException> message) {

        log.info("ErrorHandler#handleFailedMessage --- message = " + message);

        final MessageHandlingException messageHandlingException = getMessageHandlingException(message);

        final ErrorHandler handler = errorHandlers.stream()
                .filter(errorHandler -> errorHandler.canHandleIt(messageHandlingException))
                .findFirst()
                .get(); // Note: this will always return an error handler.

        handler.handle(messageHandlingException);
    }

    private MessageHandlingException getMessageHandlingException(Message<MessagingException> message) {

        MessagingException messagingException = message.getPayload();

        if (messagingException instanceof MessageHandlingException) {

            return (MessageHandlingException) messagingException;

        } else { // try to unwind it...

            Throwable requiredThrowable = messagingException.getCause();

            while (!(requiredThrowable instanceof MessageHandlingException)) {
                requiredThrowable = requiredThrowable.getCause();
            }

            return (MessageHandlingException) requiredThrowable;
        }
    }

}
