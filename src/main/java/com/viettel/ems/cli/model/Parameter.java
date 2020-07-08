package com.viettel.ems.cli.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "para")
@XmlAccessorType(XmlAccessType.FIELD)
public class Parameter {
    @XmlAttribute
    private int index;

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String description;

    @XmlAttribute
    private String type; // list, number(range), text
}
