package com.eresearch.repositorer.transformer.dto;

import com.eresearch.repositorer.dto.repositorer.request.RepositorerFindDto;

import java.util.Collection;


public class RepositorerFindDtos {

    private Collection<RepositorerFindDto> dtos;

    public RepositorerFindDtos(Collection<RepositorerFindDto> dtos) {
        this.dtos = dtos;
    }

    public RepositorerFindDtos() {
    }

    public Collection<RepositorerFindDto> getDtos() {
        return dtos;
    }

    public void setDtos(Collection<RepositorerFindDto> dtos) {
        this.dtos = dtos;
    }
}
