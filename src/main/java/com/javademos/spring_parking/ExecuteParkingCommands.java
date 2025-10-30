package com.javademos.spring_parking;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.*;
import java.util.*;

@Component
public class ExecuteParkingCommands implements CommandLineRunner {

    final int PARKING_COMMANDS_FILE_INDEX = 0;
    final int PARKING_OUTPUT_FILE_INDEX = 1;

    @Override
    public void run(String... args) throws Exception {
        List<String> arguments = new ArrayList<>(Arrays.asList(args));
        if (arguments.isEmpty()) {
            System.out.println("Usage: java -jar parking-lot.jar <parking commands file> <optional create output file>");
            return;
        }
        Path file = Paths.get(arguments.get(PARKING_COMMANDS_FILE_INDEX));
        var responses = Files.readAllLines(file).stream()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .map(this::getSystemResponseFromCommand)
                .filter(response -> !response.isBlank())
                .toList();
        responses.forEach(System.out::println);
        if (arguments.size() > 1) {
            Path outputFile = Paths.get(args[PARKING_OUTPUT_FILE_INDEX]);
            Files.write(outputFile, responses);
        }
    }

    private String getSystemResponseFromCommand (String commandLine) {
        String REGEX_PATTERN_MULTI_WHITESPACE = "\\s+";
        List<String> splitCommandLine = new ArrayList<>(Arrays.asList(commandLine.split(REGEX_PATTERN_MULTI_WHITESPACE)));
        String commandAction = splitCommandLine.get(0);
        List<String> commandArgs = splitCommandLine.subList(1, splitCommandLine.size());
        String systemResponse;
        switch (commandAction) {
            case "create_parking_lot" -> systemResponse = runCommandCreateParkingLot(commandArgs);
            case "park" -> systemResponse = runCommandPark(commandArgs);
            case "leave" -> systemResponse = runCommandLeave(commandArgs);
            case "status" -> systemResponse = runCommandStatus(commandArgs);
            case "plate_numbers_for_cars_with_colour" -> systemResponse = runCommandPlateNumbersForCarsWithColour(commandArgs);
            case "slot_numbers_for_cars_with_colour" -> systemResponse = runCommandSlotNumbersForCarsWithColour(commandArgs);
            case "slot_number_for_registration_number" -> systemResponse = runCommandSlotNumbersForCarsWithRegistrationNumber(commandArgs);
            default -> systemResponse = "Unknown command: "+commandAction;
        }
        return systemResponse;
    }

    private String runCommandCreateParkingLot(List<String> commandArgs) {
        return "";
    }

    private String runCommandPark(List<String> commandArgs) {
        return "";
    }

    private String runCommandLeave(List<String> commandArgs) {
        return "";
    }

    private String runCommandStatus(List<String> commandArgs) {
        return "";
    }

    private String runCommandPlateNumbersForCarsWithColour(List<String> commandArgs) {
        return "";
    }

    private String runCommandSlotNumbersForCarsWithColour(List<String> commandArgs) {
        return "";
    }

    private String runCommandSlotNumbersForCarsWithRegistrationNumber(List<String> commandArgs) {
        return "";
    }

}
