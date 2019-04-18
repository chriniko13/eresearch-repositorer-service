package com.eresearch.repositorer.repository;


import com.eresearch.repositorer.domain.lookup.NameLookup;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NamesLookupRepository extends MongoRepository<NameLookup, String> {

    NameLookup findNamesLookupByTransactionIdEquals(String transactionId);

    List<NameLookup> findAllByFirstnameEqualsAndInitialsEqualsAndSurnameEquals(String firstname, String initial, String surname);
}
