package com.eresearch.repositorer.resource;

import com.eresearch.repositorer.dto.repositorer.request.ErrorReportFilenameDto;
import com.eresearch.repositorer.dto.repositorer.request.ErrorReportSearchDto;
import com.eresearch.repositorer.dto.repositorer.response.ErrorReportDeleteOperationResultDto;
import com.eresearch.repositorer.dto.repositorer.response.RandomEntriesResponseDto;
import com.eresearch.repositorer.dto.repositorer.response.RetrievedErrorReportDtos;
import com.eresearch.repositorer.exception.data.RepositorerValidationException;
import com.eresearch.repositorer.service.ErrorReportService;
import com.eresearch.repositorer.validator.Validator;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Log4j
@RestController
@RequestMapping("/repositorer/error-reports")
public class ErrorReportResourceImpl implements ErrorReportResource {

    private static final String DEV_PROFILE_NOT_ACTIVE_MESSAGE = "Method could not be executed, because dev profile is not active.";

    @Autowired
    private Environment environment;

    @Autowired
    private ErrorReportService errorReportService;

    @Autowired
    private Validator<ErrorReportSearchDto> errorReportSearchDtoValidator;

    @Autowired
    private Validator<ErrorReportFilenameDto> errorReportFilenameDtoValidator;

    @RequestMapping(method = RequestMethod.POST, path = "/find", consumes = {
            MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public @ResponseBody
    ResponseEntity<RetrievedErrorReportDtos> find(@RequestBody ErrorReportSearchDto errorReportSearchDto,
                                                  @RequestParam(name = "full-fetch", defaultValue = "false") boolean fullFetch) throws RepositorerValidationException {

        try {
            errorReportSearchDtoValidator.validate(errorReportSearchDto);
            return ResponseEntity.ok(new RetrievedErrorReportDtos(errorReportService.find(errorReportSearchDto, fullFetch)));
        } catch (RepositorerValidationException e) {
            log.error("ErrorReportResourceImpl#find(ErrorReportSearchDto) --- error occurred.", e);
            throw e;
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/find-all", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public @ResponseBody
    ResponseEntity<RetrievedErrorReportDtos> findAll(@RequestParam(name = "full-fetch", defaultValue = "false") boolean fullFetch) {
        return ResponseEntity.ok(new RetrievedErrorReportDtos(errorReportService.findAll(fullFetch)));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/add-random-entries", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public @ResponseBody
    ResponseEntity<RandomEntriesResponseDto> addRandomEntriesForTesting() {


        boolean isDevProfileActive = Arrays.stream(environment.getActiveProfiles()).anyMatch(activeProfile -> activeProfile.equals("dev"));
        if (!isDevProfileActive) {
            return ResponseEntity.ok(new RandomEntriesResponseDto(DEV_PROFILE_NOT_ACTIVE_MESSAGE));
        }

        return ResponseEntity.ok(errorReportService.addRandomEntriesForTesting());
    }

    @RequestMapping(method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public @ResponseBody
    ResponseEntity<ErrorReportDeleteOperationResultDto> deleteAll() {
        ErrorReportDeleteOperationResultDto result = errorReportService.deleteAll();
        return ResponseEntity.ok(result);
    }

    @RequestMapping(method = RequestMethod.DELETE,
            consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE},
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public @ResponseBody
    ResponseEntity<ErrorReportDeleteOperationResultDto> delete(@RequestBody ErrorReportFilenameDto errorReportFilenameDto)
            throws RepositorerValidationException {

        try {
            errorReportFilenameDtoValidator.validate(errorReportFilenameDto);

            final ErrorReportDeleteOperationResultDto result = errorReportService.delete(errorReportFilenameDto);
            return ResponseEntity.ok(result);

        } catch (RepositorerValidationException e) {
            log.error("ErrorReportResourceImpl#delete(ErrorReportFilenameDto) --- error occurred.", e);
            throw e;
        }
    }
}
