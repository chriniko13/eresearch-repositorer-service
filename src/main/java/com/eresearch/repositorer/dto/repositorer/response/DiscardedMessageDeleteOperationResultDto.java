package com.eresearch.repositorer.dto.repositorer.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiscardedMessageDeleteOperationResultDto {

    private boolean operationSuccess;
    private DiscardedMessageDeleteOperationStatus discardedMessageDeleteOperationStatus;
}
