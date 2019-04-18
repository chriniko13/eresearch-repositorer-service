package com.eresearch.repositorer.resource;

import com.eresearch.repositorer.domain.lookup.NameLookup;
import com.eresearch.repositorer.dto.repositorer.request.NameLookupSearchDto;
import com.eresearch.repositorer.dto.repositorer.response.NameLookupDeleteOperationResultDto;
import com.eresearch.repositorer.dto.repositorer.response.RandomEntriesResponseDto;
import com.eresearch.repositorer.dto.repositorer.response.RetrievedNameLookupDtos;
import com.eresearch.repositorer.exception.data.RepositorerValidationException;
import com.eresearch.repositorer.service.NameLookupService;
import com.eresearch.repositorer.validator.Validator;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collections;

@Log4j
@RestController
@RequestMapping("/repositorer/name-lookups")
public class NameLookupResourceImpl implements NameLookupResource {

    private static final String DEV_PROFILE_NOT_ACTIVE_MESSAGE = "Method could not be executed, because dev profile is not active.";

    @Autowired
    private Environment environment;

    @Autowired
    private NameLookupService nameLookupService;

    @Autowired
    private Validator<NameLookupSearchDto> nameLookupSearchDtoValidator;

    @RequestMapping(method = RequestMethod.POST, path = "/find", consumes = {
            MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public @ResponseBody
    ResponseEntity<RetrievedNameLookupDtos> find(@RequestBody NameLookupSearchDto nameLookupSearchDto)
            throws RepositorerValidationException {

        try {
            nameLookupSearchDtoValidator.validate(nameLookupSearchDto);

            NameLookup retrievedNameLookup = nameLookupService.find(nameLookupSearchDto);

            return ResponseEntity.ok(new RetrievedNameLookupDtos(Collections.singletonList(retrievedNameLookup)));

        } catch (RepositorerValidationException e) {
            log.error("NameLookupResourceImpl#find(NameLookupSearchDto) --- error occurred.", e);
            throw e;
        }

    }

    @RequestMapping(method = RequestMethod.GET, path = "/find-all", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public @ResponseBody
    ResponseEntity<RetrievedNameLookupDtos> findAll() {
        return ResponseEntity.ok(new RetrievedNameLookupDtos(nameLookupService.findAll()));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/add-random-entries", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public @ResponseBody
    ResponseEntity<RandomEntriesResponseDto> addRandomEntriesForTesting() {
        boolean isDevProfileActive = Arrays.stream(environment.getActiveProfiles()).anyMatch(activeProfile -> activeProfile.equals("dev"));
        if (!isDevProfileActive) {
            return ResponseEntity.ok(new RandomEntriesResponseDto(DEV_PROFILE_NOT_ACTIVE_MESSAGE));
        }

        return ResponseEntity.ok(nameLookupService.addRandomEntriesForTesting());
    }

    @RequestMapping(method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public @ResponseBody
    ResponseEntity<NameLookupDeleteOperationResultDto> deleteAll() {
        NameLookupDeleteOperationResultDto result = nameLookupService.deleteAll();
        return ResponseEntity.ok(result);
    }

    @RequestMapping(method = RequestMethod.DELETE,
            consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE},
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public @ResponseBody
    ResponseEntity<NameLookupDeleteOperationResultDto> delete(@RequestBody NameLookupSearchDto nameLookupSearchDto)
            throws RepositorerValidationException {

        try {
            nameLookupSearchDtoValidator.validate(nameLookupSearchDto);

            final NameLookupDeleteOperationResultDto result = nameLookupService.delete(nameLookupSearchDto);

            return ResponseEntity.ok(result);

        } catch (RepositorerValidationException e) {
            log.error("NameLookupResourceImpl#delete(NameLookupSearchDto) --- error occurred.", e);
            throw e;
        }
    }
}
