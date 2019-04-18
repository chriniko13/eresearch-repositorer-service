package com.eresearch.repositorer.repository;

import com.eresearch.repositorer.domain.retry.RetryEntry;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface RetryEntryRepository extends MongoRepository<RetryEntry, String> {

    RetryEntry findRetryEntryByFirstnameEqualsAndInitialsEqualsAndSurnameEquals(String firstname, String initials, String surname);
}
