package com.eresearch.repositorer.application.actuator.health;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Log4j
@Component
public class EresearchRepositorerHealthCheck extends AbstractHealthIndicator {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        this.performBasicHealthChecks();

        Optional<Exception> ex = this.specificHealthCheck();

        if (ex.isPresent()) {
            builder.down(ex.get());
        } else {
            builder.up();
        }
    }

    private void performBasicHealthChecks() {
        //check disk...
        DiskSpaceHealthIndicatorProperties diskSpaceHealthIndicatorProperties
                = new DiskSpaceHealthIndicatorProperties();
        diskSpaceHealthIndicatorProperties.setThreshold(10737418240L); /*10 GB*/
        new DiskSpaceHealthIndicator(diskSpaceHealthIndicatorProperties);

        //check datasource...
        new MongoHealthIndicator(mongoTemplate);

        //check jms (active mq) is up...
        new JmsHealthIndicator(jmsTemplate.getConnectionFactory());
    }

    private Optional<Exception> specificHealthCheck() {
        //Note: add more thing to test for health if needed.
        return Optional.empty();
    }
}
