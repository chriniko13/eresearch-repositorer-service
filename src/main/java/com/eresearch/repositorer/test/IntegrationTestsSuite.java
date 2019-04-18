package com.eresearch.repositorer.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Log4j
@Component
public class IntegrationTestsSuite {

    @Autowired
    private AuthorMatcherIntegrationTest authorMatcherIntegrationTest;

    @Autowired
    private DblpConsumerIntegrationTest dblpConsumerIntegrationTest;

    @Autowired
    private ScienceDirectIntegrationTest scienceDirectIntegrationTest;

    @Autowired
    private ScopusIntegrationTest scopusIntegrationTest;

    @Autowired
    private ElsevierAuthorIntegrationTest elsevierAuthorIntegrationTest;

    public void runSuite() {

        //NOTE: add your scenarios or tests if needed...
        try {

            authorMatcherIntegrationTest.testIntegrationWithAuthorMatcher();
            dblpConsumerIntegrationTest.testDblpConsumerIntegration();
            scienceDirectIntegrationTest.testScienceDirectIntegration();
            scopusIntegrationTest.testScopusIntegration();
            elsevierAuthorIntegrationTest.testIntegrationWithElsevierAuthor();

        } catch (JsonProcessingException e) {
            log.error("IntegrationTestsSuite#runSuite --- error occurred.", e);
        }
    }

}
