package com.eresearch.repositorer.transformer.results.elsevierauthor;

import com.eresearch.repositorer.dto.elsevierauthor.response.*;
import com.eresearch.repositorer.exception.business.RepositorerBusinessException;
import com.eresearch.repositorer.exception.error.RepositorerError;
import com.eresearch.repositorer.transformer.dto.ElsevierAuthorResultsTransformerDto;
import com.eresearch.repositorer.transformer.results.ResultsTransformer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Log4j
@Component
public class ElsevierAuthorResultsTransformer implements ResultsTransformer {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Message<?> transform(Message<?> message) {

        try {

            log.info("ElsevierAuthorResultsTransformer --> " + Thread.currentThread().getName());

            final AuthorFinderQueueResultDto authorFinderQueueResultDto = deserializeMessage(message);

            final List<String> elsevierAuthorIds = extractElsevierAuthorIds(authorFinderQueueResultDto);

            final String transactionId = authorFinderQueueResultDto.getTransactionId();

            return MessageBuilder
                    .withPayload(new ElsevierAuthorResultsTransformerDto(elsevierAuthorIds))
                    .setHeader(TRANSACTION_ID, transactionId)
                    .build();

        } catch (IOException error) {

            log.error("ElsevierAuthorResultsTransformer#deserializeMessage --- error occurred", error);

            throw new RepositorerBusinessException(RepositorerError.COULD_NOT_DESERIALIZE_MESSAGE,
                    RepositorerError.COULD_NOT_DESERIALIZE_MESSAGE.getMessage(),
                    error,
                    this.getClass().getName());
        }
    }

    private AuthorFinderQueueResultDto deserializeMessage(Message<?> message) throws IOException {

        final String resultAsString = (String) message.getPayload();
        return objectMapper.readValue(resultAsString, new TypeReference<AuthorFinderQueueResultDto>() {
        });
    }

    private List<String> extractElsevierAuthorIds(AuthorFinderQueueResultDto authorFinderQueueResultDto) {

        final List<String> elsevierAuthorIds = new ArrayList<>();

        AuthorFinderResultsDto authorFinderResultsDto = authorFinderQueueResultDto.getAuthorFinderResultsDto();

        if (!authorFinderResultsDto.getOperationResult()) {
            throw new RepositorerBusinessException(RepositorerError.COULD_NOT_PERFORM_OPERATION,
                    RepositorerError.COULD_NOT_PERFORM_OPERATION.getMessage(),
                    this.getClass().getName());
        }

        for (AuthorSearchViewResultsDto authorSearchViewResultsDto : authorFinderResultsDto.getResults()) {

            AuthorSearchViewDto authorSearchViewDto = authorSearchViewResultsDto.getAuthorSearchViewDto();

            if (noResultsToProcess(authorSearchViewDto)) continue;

            for (AuthorSearchViewEntry authorSearchViewEntry : authorSearchViewDto.getEntries()) {

                //this will contain info such as: "dc:identifier": "AUTHOR_ID:23007591800"
                String dcIdentifier = authorSearchViewEntry.getDcIdentifier();
                final String elsevierAuthorId = dcIdentifier.split(":")[1];

                elsevierAuthorIds.add(elsevierAuthorId);
            }
        }

        return elsevierAuthorIds;
    }

    private boolean noResultsToProcess(AuthorSearchViewDto authorSearchViewDto) {
        return "0".equals(authorSearchViewDto.getTotalResults())
                && "0".equals(authorSearchViewDto.getStartIndex())
                && "0".equals(authorSearchViewDto.getItemsPerPage())
                && authorSearchViewDto.getEntries() != null
                && authorSearchViewDto.getEntries().size() == 1
                && authorSearchViewDto.getEntries().iterator().next().getError().equals("Result set was empty");
    }
}
