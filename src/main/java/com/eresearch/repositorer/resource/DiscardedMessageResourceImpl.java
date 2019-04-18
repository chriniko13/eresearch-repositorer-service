package com.eresearch.repositorer.resource;

import com.eresearch.repositorer.dto.repositorer.request.DiscardedMessageSearchDto;
import com.eresearch.repositorer.dto.repositorer.request.DiscaredMessageFilenameDto;
import com.eresearch.repositorer.dto.repositorer.response.DiscardedMessageDeleteOperationResultDto;
import com.eresearch.repositorer.dto.repositorer.response.RandomEntriesResponseDto;
import com.eresearch.repositorer.dto.repositorer.response.RetrievedDiscardedMessageDto;
import com.eresearch.repositorer.dto.repositorer.response.RetrievedDiscardedMessageDtos;
import com.eresearch.repositorer.exception.data.RepositorerValidationException;
import com.eresearch.repositorer.service.DiscardedMessageService;
import com.eresearch.repositorer.validator.Validator;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collection;

@Log4j
@RestController
@RequestMapping("/repositorer/discarded-messages")
public class DiscardedMessageResourceImpl implements DiscardedMessageResource {

    private static final String DEV_PROFILE_NOT_ACTIVE_MESSAGE = "Method could not be executed, because dev profile is not active.";

    @Autowired
    private Environment environment;

    @Autowired
    private DiscardedMessageService discardedMessageService;

    @Autowired
    private Validator<DiscardedMessageSearchDto> discardedMessageSearchDtoValidator;

    @Autowired
    private Validator<DiscaredMessageFilenameDto> discaredMessageFilenameDtoValidator;

    @RequestMapping(method = RequestMethod.POST, path = "/find", consumes = {
            MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public @ResponseBody
    ResponseEntity<RetrievedDiscardedMessageDtos> find(@RequestBody DiscardedMessageSearchDto discardedMessageSearchDto,
                                                       @RequestParam(name = "full-fetch", defaultValue = "false") boolean fullFetch)
            throws RepositorerValidationException {

        try {
            discardedMessageSearchDtoValidator.validate(discardedMessageSearchDto);

            Collection<RetrievedDiscardedMessageDto> retrievedDiscardedMessageDtos = discardedMessageService.find(discardedMessageSearchDto, fullFetch);

            return ResponseEntity.ok(new RetrievedDiscardedMessageDtos(retrievedDiscardedMessageDtos));

        } catch (RepositorerValidationException e) {
            log.error("DiscardedMessageResourceImpl#find(DiscardedMessageSearchDto) --- error occurred.", e);
            throw e;
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/find-all", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public @ResponseBody
    ResponseEntity<RetrievedDiscardedMessageDtos> findAll(@RequestParam(name = "full-fetch", defaultValue = "false") boolean fullFetch) {
        return ResponseEntity.ok(new RetrievedDiscardedMessageDtos(discardedMessageService.findAll(fullFetch)));
    }

    @RequestMapping(method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public @ResponseBody
    ResponseEntity<DiscardedMessageDeleteOperationResultDto> deleteAll() {

        DiscardedMessageDeleteOperationResultDto result = discardedMessageService.deleteAll();

        return ResponseEntity.ok(result);
    }

    @RequestMapping(method = RequestMethod.DELETE,
            consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE},
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public @ResponseBody
    ResponseEntity<DiscardedMessageDeleteOperationResultDto> delete(@RequestBody DiscaredMessageFilenameDto discaredMessageFilenameDto)
            throws RepositorerValidationException {

        try {
            discaredMessageFilenameDtoValidator.validate(discaredMessageFilenameDto);

            final DiscardedMessageDeleteOperationResultDto result = discardedMessageService.delete(discaredMessageFilenameDto);

            return ResponseEntity.ok(result);

        } catch (RepositorerValidationException e) {
            log.error("DiscardedMessageResourceImpl#delete(DiscaredMessageFilenameDto) --- error occurred.", e);
            throw e;
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/add-random-entries", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public @ResponseBody
    ResponseEntity<RandomEntriesResponseDto> addRandomEntriesForTesting() {

        boolean isDevProfileActive = Arrays.stream(environment.getActiveProfiles()).anyMatch(activeProfile -> activeProfile.equals("dev"));
        if (!isDevProfileActive) {
            return ResponseEntity.ok(new RandomEntriesResponseDto(DEV_PROFILE_NOT_ACTIVE_MESSAGE));
        }

        return ResponseEntity.ok(discardedMessageService.addRandomEntriesForTesting());
    }
}
