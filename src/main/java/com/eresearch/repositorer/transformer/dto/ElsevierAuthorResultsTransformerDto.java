package com.eresearch.repositorer.transformer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Collection;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class ElsevierAuthorResultsTransformerDto {
    private Collection<String> elsevierAuthorIds;
}
