package com.eresearch.repositorer.error.handler;

import com.eresearch.repositorer.domain.error.ErrorReport;
import com.eresearch.repositorer.exception.business.RepositorerBusinessException;
import com.eresearch.repositorer.exception.error.RepositorerError;
import com.eresearch.repositorer.repository.ErrorReportRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Order(1)
@Component
@Log4j
public class RepositorerBusinessExceptionErrorHandler extends ErrorHandler {

    public RepositorerBusinessExceptionErrorHandler(ErrorReportRepository errorReportRepository, Clock clock) {
        super(errorReportRepository, clock);
    }

    @Override
    public boolean canHandleIt(MessageHandlingException messageHandlingException) {
        return messageHandlingException.getCause() instanceof RepositorerBusinessException;
    }

    @Override
    public void handle(MessageHandlingException messageHandlingException) {
        log.info("RepositorerBusinessExceptionErrorHandler#handle");

        RepositorerBusinessException businessException = (RepositorerBusinessException) messageHandlingException.getCause();

        String errorStacktrace = getErrorStacktrace(businessException);

        RepositorerError repositorerError = businessException.getRepositorerError();
        String crashedComponentName = businessException.getCrashedComponentName();

        errorReportRepository.store(new ErrorReport(
                UUID.randomUUID().toString(),
                Instant.now(clock),
                businessException.toString(),
                repositorerError,
                crashedComponentName,
                errorStacktrace,
                messageHandlingException.getFailedMessage().toString())
        );
    }
}
