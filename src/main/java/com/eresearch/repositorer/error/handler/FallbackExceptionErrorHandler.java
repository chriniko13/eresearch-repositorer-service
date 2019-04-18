package com.eresearch.repositorer.error.handler;

import com.eresearch.repositorer.domain.error.ErrorReport;
import com.eresearch.repositorer.exception.error.RepositorerError;
import com.eresearch.repositorer.repository.ErrorReportRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Order(2)
@Component
@Log4j
public class FallbackExceptionErrorHandler extends ErrorHandler {

    private static final String UNIDENTIFIED_CRASHED_COMPONENT = "UNIDENTIFIED_CRASHED_COMPONENT";

    public FallbackExceptionErrorHandler(ErrorReportRepository errorReportRepository,
                                         Clock clock) {
        super(errorReportRepository, clock);
    }

    @Override
    public boolean canHandleIt(MessageHandlingException messageHandlingException) {
        return messageHandlingException.getCause() != null;
    }

    @Override
    public void handle(MessageHandlingException messageHandlingException) {

        log.info("RepositorerBusinessExceptionErrorHandler#handle");

        Throwable businessException = messageHandlingException.getCause();

        String errorStacktrace = getErrorStacktrace(businessException);

        errorReportRepository.store(new ErrorReport(
                UUID.randomUUID().toString(),
                Instant.now(clock),
                businessException.toString(),
                RepositorerError.UNIDENTIFIED_ERROR,
                UNIDENTIFIED_CRASHED_COMPONENT,
                errorStacktrace,
                messageHandlingException.getFailedMessage().toString())
        );
    }
}
