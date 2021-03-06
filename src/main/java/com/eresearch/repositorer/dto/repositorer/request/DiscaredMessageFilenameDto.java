package com.eresearch.repositorer.dto.repositorer.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiscaredMessageFilenameDto {

    @JsonProperty("filename")
    private String filename;
}
