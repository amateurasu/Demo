package com.viettel.ems.cli.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;

@Data
@NoArgsConstructor
@XmlRootElement(name = "macro")
@XmlAccessorType(XmlAccessType.FIELD)
public class Macro {
    @XmlElement
    private Command command;
}
