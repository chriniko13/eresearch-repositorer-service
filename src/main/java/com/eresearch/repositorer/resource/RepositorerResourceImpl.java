package com.eresearch.repositorer.resource;

import com.eresearch.repositorer.dto.repositorer.request.RepositorerFindDto;
import com.eresearch.repositorer.dto.repositorer.request.RepositorerFindDtos;
import com.eresearch.repositorer.dto.repositorer.response.RepositorerImmediateResultDto;
import com.eresearch.repositorer.exception.data.RepositorerValidationException;
import com.eresearch.repositorer.service.RepositorerExtractor;
import com.eresearch.repositorer.validator.Validator;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j
@RestController
@RequestMapping("/repositorer")
public class RepositorerResourceImpl implements RepositorerResource {

    private static final String EXTRACTION_FIRED = "Extraction fired.";

    @Autowired
    private Validator<RepositorerFindDto> repositorerFindDtoValidator;

    @Autowired
    private Validator<RepositorerFindDtos> repositorerFindDtosValidator;

    @Autowired
    private RepositorerExtractor repositorerExtractor;

    @RequestMapping(method = RequestMethod.POST, path = "/extract", consumes = {
            MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public @ResponseBody
    ResponseEntity<RepositorerImmediateResultDto> extractAuthorInfo(
            @RequestBody RepositorerFindDto dto,
            @RequestHeader(required = false, name = "Transaction-Id") String transactionId) throws RepositorerValidationException {

        try {
            repositorerFindDtoValidator.validate(dto);

            repositorerExtractor.extractAuthorInfo(dto, transactionId);

            return ResponseEntity.ok(new RepositorerImmediateResultDto(EXTRACTION_FIRED));

        } catch (RepositorerValidationException e) {
            log.error("RepositorerResourceImpl#extractAuthorInfo(RepositorerFindDto) --- error occurred.", e);
            throw e;
        }
    }

    @RequestMapping(method = RequestMethod.POST, path = "/extract/list", consumes = {
            MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public @ResponseBody
    ResponseEntity<RepositorerImmediateResultDto> extractAuthorInfo(
            @RequestBody RepositorerFindDtos repositorerFindDtos) throws RepositorerValidationException {

        try {
            repositorerFindDtosValidator.validate(repositorerFindDtos);

            repositorerExtractor.extractAuthorInfo(repositorerFindDtos);

            return ResponseEntity.ok(new RepositorerImmediateResultDto(EXTRACTION_FIRED));

        } catch (RepositorerValidationException ex) {
            log.error("RepositorerResourceImpl#extractAuthorInfo(RepositorerFindDtos) --- error occurred.", ex);
            throw ex;
        }
    }
}
