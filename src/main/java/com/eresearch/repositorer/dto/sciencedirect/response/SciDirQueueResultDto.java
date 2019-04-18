package com.eresearch.repositorer.dto.sciencedirect.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SciDirQueueResultDto {

    private String transactionId;
    private String exceptionMessage;
    private ElsevierScienceDirectConsumerResultsDto sciDirResultsDto;
}
