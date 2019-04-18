package com.eresearch.repositorer.transformer.results.dblp.processor.common;

import com.eresearch.repositorer.exception.business.RepositorerBusinessException;
import com.eresearch.repositorer.exception.error.RepositorerError;


public interface ObjectAcceptor {

    static CommonDblpSource isAcceptedObject(Object o) {
        if (o instanceof CommonDblpSource) {
            return (CommonDblpSource) o;
        }

        throw new RepositorerBusinessException(RepositorerError.APPLICATION_NOT_IN_CORRECT_STATE,
                RepositorerError.APPLICATION_NOT_IN_CORRECT_STATE.getMessage(),
                ObjectAcceptor.class.getName());
    }
}
