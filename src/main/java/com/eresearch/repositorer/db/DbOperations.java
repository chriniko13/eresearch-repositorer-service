package com.eresearch.repositorer.db;

import com.eresearch.repositorer.repository.EresearchRepositorerRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@Log4j
public class DbOperations {

    @Value("${clean.db.on.startup}")
    private String cleanDbOnStartup;

    private final ApplicationContext applicationContext;

    @Autowired
    public DbOperations(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void runTask() {

        log.info("DbOperations#runTask --- started execution...");

        if (Boolean.valueOf(cleanDbOnStartup)) {

            log.info("DbOperations#runTask --- will clean db on startup...");

            //first delete contents of MongoRepositories...
            Map<String, MongoRepository> mongoRepositoryBeansMap = applicationContext.getBeansOfType(MongoRepository.class);
            mongoRepositoryBeansMap.values().forEach(CrudRepository::deleteAll);

            //then delete contents of EresearchRepositorerRepositories...
            Map<String, EresearchRepositorerRepository> eresesearchRepositorerRepositoryBeansMap = applicationContext.getBeansOfType(EresearchRepositorerRepository.class);
            eresesearchRepositorerRepositoryBeansMap.values().forEach(EresearchRepositorerRepository::deleteAll);
        }
    }

}
