package com.eresearch.repositorer.error.scheduler;

import com.eresearch.repositorer.domain.retry.RetryEntry;
import com.eresearch.repositorer.dto.repositorer.request.RepositorerFindDto;
import com.eresearch.repositorer.gateway.AuthorExtractor;
import com.eresearch.repositorer.repository.RetryEntryRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Clock;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Log4j
@Component
public class RecoveryEntryHealerScheduler {

    @Autowired
    private RetryEntryRepository retryEntryRepository;

    @Autowired
    private AuthorExtractor authorExtractor;

    @Value("${healer.scheduler.enabled}")
    private String isHealerSchedulerEnabled;

    @Autowired
    private Clock clock;

    @Value("${healer.scheduler.processing.wait}")
    private String schedulerProcessingWait;

    private long schedulerProcessingWaitLong;

    @PostConstruct
    public void init() {
        schedulerProcessingWaitLong = Long.parseLong(schedulerProcessingWait);
    }

    /*
        NOTE:
        crone expression => second, minute, hour, day of month, month, day(s) of week


        ADDITIONAL NOTES:
        1) (*) means match any
        2) * / X means 'every X'
        3) ? ("no specific value") - useful when you need to specify something in one
        of the two fields in which the character is allowed,
        but not the other. For example, if I want my trigger to fire on a particular day
        of the month (say, the 10th), but don't care what day of the week that happens to be,
        I would put "10" in the day-of-month field, and "?" in the day-of-week field.

            +-------------------- second (0 - 59)
            |  +----------------- minute (0 - 59)
            |  |  +-------------- hour (0 - 23)
            |  |  |  +----------- day of month (1 - 31)
            |  |  |  |  +-------- month (1 - 12)
            |  |  |  |  |  +----- day of week (0 - 6) (Sunday=0 or 7)
            |  |  |  |  |  |  +-- year [optional]
            |  |  |  |  |  |  |
            *  *  *  *  *  *  * command to be executed

     */
    @Scheduled(cron = "0 0/10 * * * *") //every ten minutes.
    public void doWork() {

        if (!Boolean.valueOf(isHealerSchedulerEnabled)) {
            return;
        }

        final List<RetryEntry> retryEntries = retryEntryRepository.findAll();
        if (retryEntries == null || retryEntries.isEmpty()) {
            return;
        }

        log.info("RecoveryEntryHealerScheduler#doWork --- fired, nameLookups = [" + retryEntries + "]");

        Collections.sort(retryEntries);

        for (RetryEntry retryEntry : retryEntries) {

            try {
                log.info("RecoveryEntryHealerScheduler#doWork --- before: " + Instant.now(clock));
                TimeUnit.MINUTES.sleep(schedulerProcessingWaitLong);
                log.info("RecoveryEntryHealerScheduler#doWork --- after: " + Instant.now(clock));

                authorExtractor.extract(RepositorerFindDto.builder()
                        .firstname(retryEntry.getFirstname())
                        .initials(retryEntry.getInitials())
                        .surname(retryEntry.getSurname()).build(), UUID.randomUUID().toString());

            } catch (InterruptedException e) {
                log.error("RecoveryEntryHealerScheduler#doWork --- error occurred.", e);
            } finally {
                retryEntryRepository.delete(retryEntry.getId());
            }
        }
    }

}
