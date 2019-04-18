package com.eresearch.repositorer.resource;


import com.eresearch.repositorer.dto.repositorer.request.RepositorerFindDto;
import com.eresearch.repositorer.dto.repositorer.request.RepositorerFindDtos;
import com.eresearch.repositorer.dto.repositorer.response.RepositorerImmediateResultDto;
import com.eresearch.repositorer.exception.data.RepositorerValidationException;
import org.springframework.http.ResponseEntity;

public interface RepositorerResource {

    ResponseEntity<RepositorerImmediateResultDto> extractAuthorInfo(RepositorerFindDto dto) throws RepositorerValidationException;

    ResponseEntity<RepositorerImmediateResultDto> extractAuthorInfo(RepositorerFindDtos dto) throws RepositorerValidationException;
}
