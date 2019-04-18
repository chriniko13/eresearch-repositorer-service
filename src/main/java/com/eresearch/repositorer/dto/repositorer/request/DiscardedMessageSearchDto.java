package com.eresearch.repositorer.dto.repositorer.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiscardedMessageSearchDto {

    @JsonProperty("date")
    private LocalDate date;

    @JsonProperty("time")
    private LocalTime time;
}
