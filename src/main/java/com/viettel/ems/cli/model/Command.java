package com.viettel.ems.cli.model;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Data
@XmlRootElement(name = "command")
@XmlAccessorType(XmlAccessType.FIELD)
public class Command {
    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "macro_code")
    private int macroCode;

    @XmlAttribute(name = "macro_name")
    private String macroName;

    @XmlAttribute(name = "command_group")
    private int commandGroup;

    @XmlAttribute(name = "para_num")
    private int paraNum;

    @XmlAttribute(name = "maindisplay")
    private int mainDisplay;

    @XmlAttribute(name = "must_give")
    private String mustGive;

    @XmlAttribute(name = "must_be_confirm")
    private String mustBeConfirmed;

    @XmlElement(name = "para")
    private List<Parameter> parameters = new ArrayList<>();
}
