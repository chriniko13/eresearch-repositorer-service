package com.eresearch.repositorer.error.handler;


import com.eresearch.repositorer.exception.business.RepositorerBusinessException;
import com.eresearch.repositorer.repository.ErrorReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Clock;

@Component
public abstract class ErrorHandler {

    final ErrorReportRepository errorReportRepository;
    final Clock clock;

    @Autowired
    public ErrorHandler(ErrorReportRepository errorReportRepository, Clock clock) {
        this.errorReportRepository = errorReportRepository;
        this.clock = clock;
    }

    public abstract boolean canHandleIt(MessageHandlingException messageHandlingException);

    public abstract void handle(MessageHandlingException messageHandlingException);

    String getErrorStacktrace(Throwable businessException) {
        StringWriter sw = new StringWriter();
        businessException.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
