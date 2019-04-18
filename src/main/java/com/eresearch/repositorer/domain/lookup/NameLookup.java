package com.eresearch.repositorer.domain.lookup;

import com.eresearch.repositorer.deserializer.InstantDeserializer;
import com.eresearch.repositorer.domain.common.NameVariant;
import com.eresearch.repositorer.serializer.InstantSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Collection;


/**
 * This domain class holds the binding between author name to perform extraction and transaction id.
 */


@NoArgsConstructor
@Getter
@Setter
@ToString(of = {"firstname", "initials", "surname"})
@EqualsAndHashCode(of = {"firstname", "initials", "surname"})

@Document(collection = "names-lookup")
public class NameLookup {

    @Id
    private String id;

    private String transactionId;

    private String firstname;
    private String initials;
    private String surname;

    private Collection<NameVariant> nameVariants;

    private NameLookupStatus nameLookupStatus;

    @JsonDeserialize(using = InstantDeserializer.class)
    @JsonSerialize(using = InstantSerializer.class)
    private Instant createdAt;

    public NameLookup(String transactionId,
                      String firstname,
                      String initials,
                      String surname,
                      Collection<NameVariant> nameVariants,
                      NameLookupStatus nameLookupStatus,
                      Instant createdAt) {
        this.transactionId = transactionId;
        this.firstname = firstname;
        this.initials = initials;
        this.surname = surname;
        this.nameVariants = nameVariants;
        this.nameLookupStatus = nameLookupStatus;
        this.createdAt = createdAt;
    }
}
