package com.eresearch.repositorer.service;


import com.eresearch.repositorer.dto.repositorer.request.RepositorerFindDto;
import com.eresearch.repositorer.dto.repositorer.request.RepositorerFindDtos;

public interface RepositorerExtractor {

    void extractAuthorInfo(RepositorerFindDto dto);

    void extractAuthorInfo(RepositorerFindDtos dtos);
}
