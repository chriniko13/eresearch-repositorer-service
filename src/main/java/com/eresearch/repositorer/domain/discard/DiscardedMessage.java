package com.eresearch.repositorer.domain.discard;


import com.eresearch.repositorer.deserializer.InstantDeserializer;
import com.eresearch.repositorer.serializer.InstantSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

/**
 * This domain class holds info about discarded message(s) (from discard channel in spring integration project) which
 * created from enterprise integration patterns, such as aggregators, etc.
 * <p>
 * In our project discarded messages are created from aggregators, when a group timeout takes place.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiscardedMessage implements Serializable {

    private String id;

    @JsonDeserialize(using = InstantDeserializer.class)
    @JsonSerialize(using = InstantSerializer.class)
    private Instant createdAt;

    private String messageToString;

}
