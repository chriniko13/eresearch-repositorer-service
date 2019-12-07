package com.eresearch.repositorer.aggregator.release;

import com.eresearch.repositorer.domain.externalsystem.DynamicExternalSystemMessagesAwaiting;
import com.eresearch.repositorer.repository.DynamicExternalSystemMessagesAwaitingRepository;
import com.eresearch.repositorer.transformer.RepositorerFindDtoPopulator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.aggregator.ReleaseStrategy;
import org.springframework.integration.store.MessageGroup;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ExternalSystemsMessagesAwaitingReleaseStrategy implements ReleaseStrategy {

    @Autowired
    private DynamicExternalSystemMessagesAwaitingRepository messagesAwaitingRepository;

    public static final int NO_OF_EXTERNAL_SYSTEMS = ExternalSystem.values().length;

    public enum ExternalSystem {
        DBLP(false),
        SCIENCE_DIRECT(false),
        ELSEVIER_SCOPUS(true);

        private final boolean dynamicNoOfMessages;

        ExternalSystem(boolean dynamicNoOfMessages) {
            this.dynamicNoOfMessages = dynamicNoOfMessages;
        }

    }

    @Value("${extract.using.author.namevariants}")
    private String extractUsingAuthorNameVariants;

    @Override
    public boolean canRelease(MessageGroup group) {

        ReleaseThresholdResult resultForNotDynamicSystems = discoverReleaseThresholdOfNotDynamicExternalSystems();

        ReleaseThresholdResult resultForDynamicSystems = discoverReleaseThresholdOfDynamicExternalSystems(group);

        if (resultForNotDynamicSystems.isReady() && resultForDynamicSystems.isReady()) {

            int messageGroupSize = group.size();

            int notDynamicSystemsResultSize = resultForNotDynamicSystems.getReleaseThreshold();
            int dynamicSystemsResultSize = resultForDynamicSystems.getReleaseThreshold();

            return messageGroupSize ==  notDynamicSystemsResultSize + dynamicSystemsResultSize;
        } else {
            return false;
        }

    }

    private ReleaseThresholdResult discoverReleaseThresholdOfNotDynamicExternalSystems() {
        final Integer messagesProducedPerNotDynamicExternalSystem
                = Boolean.valueOf(extractUsingAuthorNameVariants)
                ? RepositorerFindDtoPopulator.NO_OF_NAME_VARIANTS : 1;

        final Integer countOfNotDynamicExternalSystems = Arrays
                .stream(ExternalSystem.values())
                .filter(externalSystem -> !externalSystem.dynamicNoOfMessages)
                .map(externalSystem -> 1)
                .reduce(0, Integer::sum);

        return new ReleaseThresholdResult(true,
                countOfNotDynamicExternalSystems * messagesProducedPerNotDynamicExternalSystem);
    }

    private ReleaseThresholdResult discoverReleaseThresholdOfDynamicExternalSystems(MessageGroup group) {

        final String transactionId = (String) group.getGroupId();

        final ReleaseThresholdResult releaseThresholdResult = new ReleaseThresholdResult(false, 0);

        for (ExternalSystem externalSystem : ExternalSystem.values()) {

            if (!externalSystem.dynamicNoOfMessages) {
                continue;
            }

            DynamicExternalSystemMessagesAwaiting result = messagesAwaitingRepository.findByTransactionIdEqualsAndExternalSystemNameEquals(
                    transactionId,
                    externalSystem.name());

            if (result == null) {
                return releaseThresholdResult;
            } else {
                releaseThresholdResult.setReleaseThreshold(releaseThresholdResult.getReleaseThreshold() + result.getNoOfMessagesAwaiting());
            }

        }

        releaseThresholdResult.setReady(true);
        return releaseThresholdResult;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private class ReleaseThresholdResult {
        private boolean ready;
        private int releaseThreshold;
    }
}


