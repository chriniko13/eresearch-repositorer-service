package com.eresearch.repositorer.dto.authormatcher.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AuthorComparisonDto {

    @JsonProperty("first-author-name")
    private AuthorNameDto firstAuthorName;

    @JsonProperty("second-author-name")
    private AuthorNameDto secondAuthorName;

}
