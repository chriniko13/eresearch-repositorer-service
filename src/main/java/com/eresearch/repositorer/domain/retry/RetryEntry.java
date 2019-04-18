package com.eresearch.repositorer.domain.retry;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;


/**
 * This domain class holds info for failed author extraction attempt(s), in order to perform retries on this/them.
 */

@Getter
@Setter
@NoArgsConstructor
@ToString(of = {"firstname", "initials", "surname", "createdAt"})

@Document(collection = "retry-entries")
public class RetryEntry implements Comparable<RetryEntry> {

    @Id
    private String id;

    private String firstname;
    private String initials;
    private String surname;

    private Instant createdAt;

    public RetryEntry(String firstname, String initials, String surname, Instant createdAt) {
        this.firstname = firstname;
        this.initials = initials;
        this.surname = surname;
        this.createdAt = createdAt;
    }

    @Override
    public int compareTo(RetryEntry other) {
        return this.getCreatedAt().compareTo(other.getCreatedAt());
    }
}
