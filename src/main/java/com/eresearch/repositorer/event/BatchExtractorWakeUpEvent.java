package com.eresearch.repositorer.event;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class BatchExtractorWakeUpEvent {

    private boolean continueWork;
}
