package com.eresearch.repositorer.domain.common;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NameVariant implements Serializable {

    private String firstname;
    private String initials;
    private String surname;
}
