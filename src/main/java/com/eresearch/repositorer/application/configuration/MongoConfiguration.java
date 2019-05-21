package com.eresearch.repositorer.application.configuration;


import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collection;
import java.util.Collections;

@Configuration
@EnableMongoRepositories(
        basePackages = {"com.eresearch.repositorer.repository"},
        createIndexesForQueryMethods = true
)
public class MongoConfiguration extends AbstractMongoConfiguration {

    @Value("${mongo.host}")
    private String host;

    @Override
    protected String getDatabaseName() {
        return "eresearch";
    }

    @Override
    public Mongo mongo() throws Exception {
        return new MongoClient(host, 27017);
    }

    @Override
    protected Collection<String> getMappingBasePackages() {
        return Collections.singletonList("com.eresearch.repositorer.domain");
    }

    @Bean
    public GridFsTemplate gridFsTemplate() throws Exception {
        return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
    }
}