package com.javademos.spring_parking.model;

import lombok.Builder;
import lombok.Value;

import java.nio.file.Path;

@Value
@Builder
public class CommandLineArguments {
    Path commandFile;
    boolean activeFlagHelp;
    boolean activeFlagVersion;
    String error;

    public boolean isInvalid() {
        return error != null;
    }
}
