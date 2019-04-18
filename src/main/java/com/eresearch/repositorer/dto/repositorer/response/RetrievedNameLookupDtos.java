package com.eresearch.repositorer.dto.repositorer.response;

import com.eresearch.repositorer.domain.lookup.NameLookup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RetrievedNameLookupDtos {

    private Collection<NameLookup> nameLookups;
}
