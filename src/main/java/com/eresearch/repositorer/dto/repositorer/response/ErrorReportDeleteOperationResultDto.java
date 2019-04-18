package com.eresearch.repositorer.dto.repositorer.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorReportDeleteOperationResultDto {

    private boolean operationSuccess;
    private ErrorReportDeleteOperationStatus errorReportDeleteOperationStatus;
}
