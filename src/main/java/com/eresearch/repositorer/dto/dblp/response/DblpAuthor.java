package com.eresearch.repositorer.dto.dblp.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import java.util.Objects;

@EqualsAndHashCode(of = {"authorName", "urlpt"})
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class DblpAuthor {

    @XmlValue
    private String authorName;

    @XmlAttribute(name = "urlpt")
    private String urlpt;

    public DblpAuthor(String value) {
        String[] splittedInfo = value.split("#");

        authorName = splittedInfo[0].split("=")[1];
        urlpt = splittedInfo[1].split("=")[1];
    }

    @Override
    public String toString() {
        return "authorName=" + authorName + "#urlpt=" + urlpt;
    }
}
