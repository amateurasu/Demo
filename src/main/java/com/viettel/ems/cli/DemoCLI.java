package com.viettel.ems.cli;

import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.OptionSpec;
import picocli.CommandLine.Model.PositionalParamSpec;
import picocli.CommandLine.ParseResult;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class DemoCLI {

    public static void main(String[] args) {
        CommandSpec cmdSpec = CommandSpec.create().name("somecommand").version("My Command v1.0");

        cmdSpec.usageMessage()
            .headerHeading("Header heading%n")
            .header("header line 1", "header line 2")
            .descriptionHeading("Description heading%n")
            .description("description line 1", "description line 2")
            .optionListHeading("Options%n")
            .parameterListHeading("Positional Parameters%n")
            .footerHeading("Footer heading%n")
            .footer("footer line 1", "footer line 2");

        cmdSpec.mixinStandardHelpOptions(true); // usageHelp and versionHelp options
        cmdSpec.addOption(OptionSpec.builder("-c", "--count")
            .paramLabel("COUNT")
            .type(int.class)
            .description("number of times to execute")
            .build());
        cmdSpec.addPositional(PositionalParamSpec.builder()
            .paramLabel("FILES")
            .type(List.class)
            .auxiliaryTypes(File.class)
            .description("The files to process")
            .build());
        CommandLine commandLine = new CommandLine(cmdSpec);

        // set an execution strategy (the run(ParseResult) method) that will be called
        // by CommandLine.execute(args) when user input was valid
        commandLine.setExecutionStrategy(DemoCLI::run);
        int exitCode = commandLine.execute(args);
        System.out.println();
        System.out.println("command return: " + exitCode);
    }

    static int run(ParseResult pr) {
        // handle requests for help or version information
        Integer helpExitCode = CommandLine.executeHelpRequest(pr);
        if (helpExitCode != null) {
            return helpExitCode;
        }

        // implement the business logic
        int count = pr.matchedOptionValue('c', 1);
        List<File> files = pr.matchedPositionalValue(0, Collections.emptyList());
        for (File f : files) {
            for (int i = 0; i < count; i++) {
                System.out.println(i + " " + f.getName());
            }
        }
        return files.size();
    }
}

