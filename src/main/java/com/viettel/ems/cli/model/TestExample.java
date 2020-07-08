package com.viettel.ems.cli.model;

import com.viettel.ems.snmp.Collector;
import lombok.val;
import picocli.CommandLine;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class TestExample {
    private static final String XML_FILE = "D:\\Projects\\JVM\\EMS\\Demo\\list\\cell\\BLK CELL.xml";

    public static void main(String[] args) throws IOException {
        String listFolder = "D:\\Projects\\JVM\\EMS\\Demo\\list";
        System.out.println("---------------------------");

        var result = Files.walk(Path.of(listFolder))
            .parallel()
            .filter(path -> path.getFileName().toString().endsWith(".xml"))
            .map(path -> {
                try {
                    parseMacro(path);
                } catch (JAXBException e) {
                    e.printStackTrace();
                }
                return null;
            })
            .collect(Collectors.toList());

        System.out.println("---------------------------");
        System.out.println(result);
    }

    private static void parseMacro(Path path) throws JAXBException {
        var context = JAXBContext.newInstance(Macro.class);

        var um = context.createUnmarshaller();
        var macro = (Macro) um.unmarshal(path.toFile());
        var str = macro.toString();
        System.out.println(str);
        // for (Employee employee : employeeList) {
        //     System.out.format("Employee: %s\n", employee.getEmpName());
        // }
    }
}
