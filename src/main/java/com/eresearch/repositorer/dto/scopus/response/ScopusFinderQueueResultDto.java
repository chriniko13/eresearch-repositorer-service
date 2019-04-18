package com.eresearch.repositorer.dto.scopus.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScopusFinderQueueResultDto {
    private String transactionId;
    private String exceptionMessage;
    private ElsevierScopusConsumerResultsDto scopusConsumerResultsDto;
}
