package com.eresearch.repositorer.transformer.dto;


import com.eresearch.repositorer.domain.record.Entry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class TransformedEntriesDto {

    private List<Entry> entries;
}
