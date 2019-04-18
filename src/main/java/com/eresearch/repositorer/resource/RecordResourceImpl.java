package com.eresearch.repositorer.resource;

import com.eresearch.repositorer.dto.repositorer.request.RecordFilenameDto;
import com.eresearch.repositorer.dto.repositorer.request.RecordSearchDto;
import com.eresearch.repositorer.dto.repositorer.response.RecordDeleteOperationResultDto;
import com.eresearch.repositorer.dto.repositorer.response.RecordSearchResultDto;
import com.eresearch.repositorer.exception.data.RepositorerValidationException;
import com.eresearch.repositorer.service.RecordService;
import com.eresearch.repositorer.validator.Validator;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j
@RestController
@RequestMapping("/repositorer/records")
public class RecordResourceImpl implements RecordResource {

    @Autowired
    private Validator<RecordSearchDto> recordSearchDtoValidator;

    @Autowired
    private RecordService recordService;

    @Autowired
    private Validator<RecordFilenameDto> recordFilenameDtoValidator;

    @RequestMapping(method = RequestMethod.POST, path = "/find", consumes = {
            MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public @ResponseBody
    ResponseEntity<RecordSearchResultDto> find(@RequestBody RecordSearchDto recordSearchDto,
                                               @RequestParam(name = "full-fetch", defaultValue = "false") boolean fullFetch)
            throws RepositorerValidationException {

        try {
            recordSearchDtoValidator.validate(recordSearchDto);

            RecordSearchResultDto recordSearchResultDto = recordService.find(recordSearchDto, fullFetch);

            return ResponseEntity.ok(recordSearchResultDto);

        } catch (RepositorerValidationException e) {
            log.error("RecordResourceImpl#find(RecordSearchDto) --- error occurred.", e);
            throw e;
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/find-by-filename", consumes = {
            MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public @ResponseBody
    ResponseEntity<RecordSearchResultDto> find(@RequestBody RecordFilenameDto recordFilenameDto,
                                               @RequestParam(name = "full-fetch", defaultValue = "false") boolean fullFetch) throws RepositorerValidationException {

        try {

            recordFilenameDtoValidator.validate(recordFilenameDto);
            RecordSearchResultDto recordSearchResultDto = recordService.find(recordFilenameDto, fullFetch);
            return ResponseEntity.ok(recordSearchResultDto);

        } catch (RepositorerValidationException e) {
            log.error("RecordResourceImpl#find(RecordFilenameDto) --- error occurred.", e);
            throw e;
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/find-all", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public @ResponseBody
    ResponseEntity<RecordSearchResultDto> findAll(@RequestParam(name = "full-fetch", defaultValue = "false") boolean fullFetch) {
        RecordSearchResultDto recordSearchResultDto = recordService.findAll(fullFetch);
        return ResponseEntity.ok(recordSearchResultDto);
    }

    @RequestMapping(method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public @ResponseBody
    ResponseEntity<RecordDeleteOperationResultDto> deleteAll() {
        RecordDeleteOperationResultDto result = recordService.deleteAll();
        return ResponseEntity.ok(result);
    }

    @RequestMapping(method = RequestMethod.DELETE,
            consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE},
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public @ResponseBody
    ResponseEntity<RecordDeleteOperationResultDto> delete(@RequestBody RecordFilenameDto recordFilenameDto) throws RepositorerValidationException {
        try {
            recordFilenameDtoValidator.validate(recordFilenameDto);

            final RecordDeleteOperationResultDto result = recordService.delete(recordFilenameDto);
            return ResponseEntity.ok(result);

        } catch (RepositorerValidationException e) {
            log.error("ErrorReportResourceImpl#delete(ErrorReportFilenameDto) --- error occurred.", e);
            throw e;
        }
    }

}
