package com.eresearch.repositorer;

import com.eresearch.repositorer.application.event.listener.*;
import com.eresearch.repositorer.db.DbOperations;
import com.eresearch.repositorer.test.IntegrationTestsSuite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource(
        value = {
                "classpath:repositorer-first-step-workflow.xml",
                "classpath:repositorer-second-step-workflow.xml",
                "classpath:repositorer-error-handling-workflow.xml",
                "classpath:repositorer-error-handling-aggregators-workflow.xml"
        }
)
public class EresearchRepositorerApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplicationBuilder springApplicationBuilder
                = new SpringApplicationBuilder(EresearchRepositorerApplication.class);

        registerApplicationListeners(springApplicationBuilder);

        springApplicationBuilder
                .web(true)
                .run(args);
    }

    private static void registerApplicationListeners(final SpringApplicationBuilder springApplicationBuilder) {
        springApplicationBuilder.listeners(new ApplicationEnvironmentPreparedEventListener());
        springApplicationBuilder.listeners(new ApplicationFailedEventListener());
        springApplicationBuilder.listeners(new ApplicationReadyEventListener());
        springApplicationBuilder.listeners(new ApplicationStartedEventListener());
        springApplicationBuilder.listeners(new BaseApplicationEventListener());
    }


    @Autowired
    private DbOperations dbOperations;

    @Autowired
    private IntegrationTestsSuite integrationTestsSuite;

    @Override
    public void run(ApplicationArguments args) {
        // integrationTestsSuite.runSuite();
        dbOperations.runTask();
    }

}
