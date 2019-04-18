package com.eresearch.repositorer.aggregator.release;

import com.eresearch.repositorer.transformer.RepositorerFindDtoPopulator;
import org.springframework.integration.aggregator.ReleaseStrategy;
import org.springframework.integration.store.MessageGroup;
import org.springframework.stereotype.Component;

@Component
public class ElsevierAuthorMessagesAwaitingReleaseStrategy implements ReleaseStrategy {

    private static final Integer OPERATIONS_PER_EXTERNAL_SYSTEM = RepositorerFindDtoPopulator.NO_OF_NAME_VARIANTS;

    private static final Integer NO_OF_EXTERNAL_SYSTEMS = ExternalSystem
            .values()
            .length;

    private enum ExternalSystem {
        ELSEVIER_AUTHOR
    }

    @Override
    public boolean canRelease(MessageGroup group) {
        return group.size() == NO_OF_EXTERNAL_SYSTEMS * OPERATIONS_PER_EXTERNAL_SYSTEM;
    }
}
