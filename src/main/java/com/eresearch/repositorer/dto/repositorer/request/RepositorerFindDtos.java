package com.eresearch.repositorer.dto.repositorer.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RepositorerFindDtos {

    @JsonProperty("names")
    private List<RepositorerFindDto> repositorerFindDtos;

}
