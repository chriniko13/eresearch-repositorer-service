package com.eresearch.repositorer.transformer;

import com.eresearch.repositorer.dto.repositorer.request.RepositorerFindDto;
import com.eresearch.repositorer.transformer.dto.RepositorerFindDtos;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * The purpose of this component is to populate the provided name for info-extraction
 * with variants.
 * <p>
 * For example if the input name is: Christos Skourlas
 * Then the expected output will be: [C Skourlas, C. Skourlas]
 * <p>
 * So we have better results in info extraction.
 */
@Log4j
@Component
public class RepositorerFindDtoPopulator {

    @Value("${extract.using.author.namevariants}")
    private String extractUsingAuthorNameVariants;

    public static final Integer NO_OF_NAME_VARIANTS = 3;

    public Message<?> populate(Message<?> message) {

        log.info("RepositorerFindDtoPopulator --> " + Thread.currentThread().getName());

        final RepositorerFindDto repositorerFindDto = (RepositorerFindDto) message.getPayload();
        final List<RepositorerFindDto> repositorerFindDtos = new LinkedList<>();

        repositorerFindDtos.add(repositorerFindDto); // first entry will always be the request from client...

        if (Boolean.valueOf(extractUsingAuthorNameVariants)) {
            final RepositorerFindDto.RepositorerFindDtoBuilder builder = RepositorerFindDto
                    .builder()
                    .surname(repositorerFindDto.getSurname())
                    .initials(repositorerFindDto.getInitials());

            //create the variants...
            final RepositorerFindDto repositorerFindDtoVariantOne = createFirstNameVariant(repositorerFindDto, builder);

            final RepositorerFindDto repositorerFindDtoVariantTwo = createSecondNameVariant(repositorerFindDto, builder);

            //save the variants...
            repositorerFindDtos.add(repositorerFindDtoVariantOne);
            repositorerFindDtos.add(repositorerFindDtoVariantTwo);
        }

        log.info("RepositorerFindDtoEnricher#populate --- fired, repositorerFindDtos = " + repositorerFindDtos);

        //construct the message...
        return MessageBuilder
                .withPayload(new RepositorerFindDtos(repositorerFindDtos))
                .copyHeaders(message.getHeaders())
                .build();
    }

    private RepositorerFindDto createFirstNameVariant(RepositorerFindDto repositorerFindDto, RepositorerFindDto.RepositorerFindDtoBuilder builder) {
        String firstnameVariantOne = repositorerFindDto.getFirstname().charAt(0) + ".";
        return builder
                .firstname(firstnameVariantOne)
                .build();
    }

    private RepositorerFindDto createSecondNameVariant(RepositorerFindDto repositorerFindDto, RepositorerFindDto.RepositorerFindDtoBuilder builder) {
        String firstnameVariantTwo = String.valueOf(repositorerFindDto.getFirstname().charAt(0));
        return builder
                .firstname(firstnameVariantTwo)
                .build();
    }

}
