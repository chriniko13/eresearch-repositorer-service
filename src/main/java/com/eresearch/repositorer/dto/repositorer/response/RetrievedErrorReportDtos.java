package com.eresearch.repositorer.dto.repositorer.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RetrievedErrorReportDtos {

    private Collection<RetrievedErrorReportDto> retrievedErrorReportDtos;
}
