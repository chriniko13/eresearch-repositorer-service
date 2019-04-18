package com.eresearch.repositorer.activator;

import com.eresearch.repositorer.domain.common.NameVariant;
import com.eresearch.repositorer.domain.lookup.NameLookup;
import com.eresearch.repositorer.domain.lookup.NameLookupStatus;
import com.eresearch.repositorer.dto.repositorer.request.RepositorerFindDto;
import com.eresearch.repositorer.repository.NamesLookupRepository;
import com.eresearch.repositorer.transformer.dto.RepositorerFindDtos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NamesLookupStorageActivator {

    private static final String TRANSACTION_ID = "Transaction-Id";

    @Autowired
    private NamesLookupRepository namesLookupRepository;

    @Autowired
    private Clock clock;

    public Message<?> store(Message<?> message) {

        //extract necessary info...
        final RepositorerFindDtos dtos = (RepositorerFindDtos) message.getPayload();
        final String transactionId = (String) message.getHeaders().get(TRANSACTION_ID);

        //grab the first original request dto from client (user of rest api)...
        final RepositorerFindDto originalRequestDto = dtos.getDtos().iterator().next();

        List<NameVariant> nameVariants = getNameVariants(dtos, originalRequestDto);

        //store the information...
        namesLookupRepository.save(new NameLookup(transactionId,
                originalRequestDto.getFirstname(),
                originalRequestDto.getInitials(),
                originalRequestDto.getSurname(),
                nameVariants,
                NameLookupStatus.PENDING,
                Instant.now(clock)));

        //return the correct message...
        return MessageBuilder
                .withPayload(dtos)
                .copyHeaders(message.getHeaders())
                .build();
    }

    private List<NameVariant> getNameVariants(RepositorerFindDtos dtos,
                                              RepositorerFindDto originalRequestDto) {

        //then collect the other dtos as name variants... (difference between two sets)
        HashSet<RepositorerFindDto> dtosAsSet = new HashSet<>(dtos.getDtos());
        dtosAsSet.removeAll(Collections.singletonList(originalRequestDto));

        return dtosAsSet
                .stream()
                .map(dto -> new NameVariant(dto.getFirstname(),
                        dto.getInitials(),
                        dto.getSurname()))
                .collect(Collectors.toList());
    }
}
