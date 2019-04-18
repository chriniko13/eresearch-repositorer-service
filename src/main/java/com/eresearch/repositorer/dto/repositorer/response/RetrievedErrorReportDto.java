package com.eresearch.repositorer.dto.repositorer.response;

import com.eresearch.repositorer.domain.error.ErrorReport;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(of = {"filename"})
@EqualsAndHashCode(of = {"filename"})

@JsonInclude(value = JsonInclude.Include.NON_NULL) //in order to not include null error report.
public class RetrievedErrorReportDto {

    private String filename;
    private ErrorReport errorReport;
}
