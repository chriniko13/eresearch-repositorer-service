package com.eresearch.repositorer.domain.error;


import com.eresearch.repositorer.deserializer.InstantDeserializer;
import com.eresearch.repositorer.exception.error.RepositorerError;
import com.eresearch.repositorer.serializer.InstantSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

/**
 * This domain class holds info about error(s) raised-produced in our service.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorReport implements Serializable {

    private String id;

    @JsonDeserialize(using = InstantDeserializer.class)
    @JsonSerialize(using = InstantSerializer.class)
    private Instant createdAt;
    private String exceptionToString;
    private RepositorerError repositorerError;
    private String crashedComponentName;
    private String errorStacktrace;

    private String failedMessage;
}
