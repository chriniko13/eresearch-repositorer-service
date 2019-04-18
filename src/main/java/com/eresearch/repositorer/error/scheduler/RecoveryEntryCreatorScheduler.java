package com.eresearch.repositorer.error.scheduler;

import com.eresearch.repositorer.domain.lookup.NameLookup;
import com.eresearch.repositorer.domain.lookup.NameLookupStatus;
import com.eresearch.repositorer.domain.retry.RetryEntry;
import com.eresearch.repositorer.repository.DynamicExternalSystemMessagesAwaitingRepository;
import com.eresearch.repositorer.repository.NamesLookupRepository;
import com.eresearch.repositorer.repository.RetryEntryRepository;
import com.google.common.collect.Sets;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Log4j
public class RecoveryEntryCreatorScheduler {

    @Autowired
    private NamesLookupRepository namesLookupRepository;

    @Autowired
    private DynamicExternalSystemMessagesAwaitingRepository externalSystemMessagesAwaitingRepository;

    @Autowired
    private RetryEntryRepository retryEntryRepository;

    @Autowired
    private Clock clock;

    @Value("${healer.scheduler.enabled}")
    private String isHealerSchedulerEnabled;

    @Value("${entry.creator.scheduler.minutes.waiting.before.recovery.mode}")
    private String minutesWaitingBeforeEnterInRecoverModeForDbConsistencyStr;

    private long minutesWaitingBeforeEnterInRecoverModeForDbConsistency;

    private Set<NameLookupStatus> nameLookupStatusesToNotProcess;

    @PostConstruct
    public void init() {
        minutesWaitingBeforeEnterInRecoverModeForDbConsistency = Long.parseLong(minutesWaitingBeforeEnterInRecoverModeForDbConsistencyStr);

        nameLookupStatusesToNotProcess = Sets.newHashSet(NameLookupStatus.COMPLETED, NameLookupStatus.TIMED_OUT);
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
    @Scheduled(cron = "0 0/5 * * * *") //every five minutes.
    public void doWork() {

        List<NameLookup> nameLookups = namesLookupRepository.findAll();
        if (nameLookups == null || nameLookups.isEmpty()) {
            return;
        }

        //keep records which are only in PENDING status....
        nameLookups = getNameLookupsWithPendingStatus(nameLookups);

        log.info("RecoveryEntryCreatorScheduler#doWork --- fired, nameLookups = [" + nameLookups + "]");

        final HashSet<NameLookup> uniqueNameLookups = Sets.newHashSet(nameLookups);
        for (NameLookup uniqueNameLookup : uniqueNameLookups) {

            Instant createdAt = uniqueNameLookup.getCreatedAt();

            ZonedDateTime zonedCreatedAt = ZonedDateTime.ofInstant(createdAt, clock.getZone());
            ZonedDateTime now = ZonedDateTime.now(clock);

            final long minutesPassed = Duration.between(zonedCreatedAt.toInstant(), now.toInstant()).toMinutes();
            final boolean shouldEnterIntoRecoverModeForDbConsistency = minutesPassed >= minutesWaitingBeforeEnterInRecoverModeForDbConsistency;

            if (shouldEnterIntoRecoverModeForDbConsistency) {
                recoverModeForDbConsistency(uniqueNameLookup, minutesPassed);
            }

        }
    }

    private List<NameLookup> getNameLookupsWithPendingStatus(List<NameLookup> nameLookups) {
        return nameLookups
                .stream()
                .filter(nameLookup -> !nameLookupStatusesToNotProcess.contains(nameLookup.getNameLookupStatus()))
                .collect(Collectors.toList());
    }

    private void recoverModeForDbConsistency(NameLookup uniqueNameLookup, long minutesPassed) {

        //Note: in order to be sure and also remove duplicated entries...
        String firstname = uniqueNameLookup.getFirstname();
        String initials = uniqueNameLookup.getInitials();
        String surname = uniqueNameLookup.getSurname();

        final List<NameLookup> nameLookupsToRemove = namesLookupRepository.findAllByFirstnameEqualsAndInitialsEqualsAndSurnameEquals(
                firstname,
                initials,
                surname);

        for (NameLookup nameLookupToRemove : nameLookupsToRemove) {

            String txId = nameLookupToRemove.getTransactionId();

            //Note: update collection if needed -> names-lookup
            NameLookup fetchedNameLookupToUpdate = namesLookupRepository.findNamesLookupByTransactionIdEquals(txId);
            if (fetchedNameLookupToUpdate.getNameLookupStatus() == NameLookupStatus.PENDING) {
                fetchedNameLookupToUpdate.setNameLookupStatus(NameLookupStatus.TIMED_OUT);
                namesLookupRepository.save(fetchedNameLookupToUpdate);
            }

            //Note: clean collection -> dynamic-external-systems-messages-awaiting
            externalSystemMessagesAwaitingRepository.deleteByTransactionIdEquals(txId);
        }

        //Note: create a new entry (if selected via properties), so another scheduler (healer) retries extraction process again in the future.
        boolean recordNotExists = retryEntryRepository.findRetryEntryByFirstnameEqualsAndInitialsEqualsAndSurnameEquals(firstname, initials, surname) == null;

        if (recordNotExists && Boolean.valueOf(isHealerSchedulerEnabled)) {
            log.info("RecoveryScheduler#doRecovery --- going to create a recovery entry, nameLookup = [ " + uniqueNameLookup + "], minutesPassed = [" + minutesPassed + "]");
            retryEntryRepository.insert(new RetryEntry(firstname, initials, surname, Instant.now(clock)));
        }
    }

}
