package com.eresearch.repositorer.aggregator;

import com.eresearch.repositorer.aggregator.release.ExternalSystemsMessagesAwaitingReleaseStrategy;
import com.eresearch.repositorer.domain.externalsystem.DynamicExternalSystemMessagesAwaiting;
import com.eresearch.repositorer.repository.DynamicExternalSystemMessagesAwaitingRepository;
import com.eresearch.repositorer.transformer.dto.ElsevierAuthorResultsTransformerDto;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Log4j
@Component
public class ElsevierAuthorResultsAggregator {

    private static final String TRANSACTION_ID = "Transaction-Id";

    @Autowired
    private Clock clock;

    @Autowired
    private DynamicExternalSystemMessagesAwaitingRepository messagesAwaitingRepository;

    public Message<?> transform(Collection<Message<?>> messages) {

        log.info("ElsevierAuthorResultsAggregator --> " + Thread.currentThread().getName());

        //extract necessary info...
        final Message<?> firstMessageOfGroup = messages.iterator().next();
        final String transactionId = (String) firstMessageOfGroup.getHeaders().get(TRANSACTION_ID);

        //find out how many messages will get from elsevier scopus (apply distinction)...
        Set<String> aggregatedElsevierAuthorIds = new LinkedHashSet<>();
        for (Message<?> message : messages) {

            final ElsevierAuthorResultsTransformerDto dto = (ElsevierAuthorResultsTransformerDto) message.getPayload();
            final Collection<String> elsevierAuthorIds = dto.getElsevierAuthorIds();

            aggregatedElsevierAuthorIds.addAll(elsevierAuthorIds);
        }

        //store the above information (how many messages we will get from elsevier scopus)...
        messagesAwaitingRepository.insert(new DynamicExternalSystemMessagesAwaiting(
                transactionId,
                ExternalSystemsMessagesAwaitingReleaseStrategy.ExternalSystem.ELSEVIER_SCOPUS.name(),
                aggregatedElsevierAuthorIds.size(),
                Instant.now(clock)));

        //return a message which has aggregated elsevier author ids...
        return MessageBuilder
                .withPayload(new ElsevierAuthorResultsTransformerDto(aggregatedElsevierAuthorIds))
                .copyHeaders(firstMessageOfGroup.getHeaders())
                .build();
    }
}
