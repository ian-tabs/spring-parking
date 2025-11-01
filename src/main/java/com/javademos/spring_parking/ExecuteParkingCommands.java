package com.javademos.spring_parking;

import com.javademos.spring_parking.model.CommandLineArguments;
import com.javademos.spring_parking.model.ParkingLotOccupant;
import com.javademos.spring_parking.model.ParkingLotSlot;
import com.javademos.spring_parking.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ExecuteParkingCommands implements CommandLineRunner {

    @Autowired
    @Qualifier("parkingServiceCommandLineImpl")
    ParkingService parkingServiceImpl;

    private static final String REGEX_PATTERN_MULTI_WHITESPACE = "\\s+";

    private static final String FLAG_HELP = "--help";
    private static final String FLAG_HELP_SHORT = "-h";
    private static final String FLAG_VERSION = "--version";
    private static final String FLAG_VERSION_SHORT = "-v";

    private static final String VERSION_INFO = "Spring Parking - CLI Interface v1.0.0";

    private static final String HOW_TO_USE = """
        Usage:
          java -jar spring-parking.jar <parking commands file> [options]

        Options:
          --help, -h      Show this help message.
          --version, -v   Show version information.
        """;

    @Override
    public void run(String... args) throws Exception {
        CommandLineArguments arguments = parseArgs(args);

        if(arguments.isInvalid()) {
            System.out.println(arguments.getError());
            return;
        }

        Path commandFile = arguments.getCommandFile();
        if (commandFile != null) {
            List<String> responses;
            try {
                responses = Files.readAllLines(commandFile).stream()
                        .map(String::trim)
                        .filter(line -> !line.isEmpty())
                        .map(this::getSystemResponseFromCommand)
                        .filter(response -> !response.isBlank())
                        .toList();
            } catch (NoSuchFileException e) {
                System.out.printf("File %s not found%n", commandFile);
                return;
            } catch (Exception e) {
                System.out.printf("Unexpected exception encountered: %s%n",e.getMessage());
                return;
            }

            responses.forEach(System.out::println);
        }

        if (arguments.isActiveFlagHelp()) {
            System.out.println(HOW_TO_USE);
        }

        if (arguments.isActiveFlagVersion()) {
            System.out.println(VERSION_INFO);
        }
    }


    private CommandLineArguments parseArgs(String... args) {
        var builder = CommandLineArguments.builder();

        Path commandFile = null;
        boolean showHelp = false;
        boolean showVersion = false;

        for (String arg : args) {
            switch (arg) {
                case FLAG_HELP, FLAG_HELP_SHORT -> showHelp = true;
                case FLAG_VERSION, FLAG_VERSION_SHORT -> showVersion = true;
                default -> {
                    if (arg.startsWith("-")) {
                        return builder.error(String.format("Unknown flag: %s", arg)).build();
                    }
                    if (commandFile != null) {
                        return builder.error(String.format(
                                "Unexpected argument: %s. %s already provided", arg, commandFile
                        )).build();
                    }
                    commandFile = Paths.get(arg);
                }
            }
        }

        boolean invalid = (commandFile == null && !showHelp && !showVersion);
        if (invalid) {
            return builder.error("No command file or flags provided. Use flag -h or --help for a User Guide").build();
        }

        return builder
                .commandFile(commandFile)
                .activeFlagHelp(showHelp)
                .activeFlagVersion(showVersion)
                .build();
    }

    public String getSystemResponseFromCommand(String commandLine) {
        List<String> split = Arrays.asList(commandLine.split(REGEX_PATTERN_MULTI_WHITESPACE));
        String action = split.get(0);
        List<String> args = split.subList(1, split.size());
        return switch (action) {
            case "create_parking_lot" -> runCommandCreateParkingLot(args);
            case "park" -> runCommandPark(args);
            case "leave" -> runCommandLeave(args);
            case "status" -> runCommandStatus();
            case "plate_numbers_for_cars_with_colour" -> runCommandPlateNumbersForCarsWithColour(args);
            case "slot_numbers_for_cars_with_colour" -> runCommandSlotNumbersForCarsWithColour(args);
            case "slot_number_for_registration_number" -> runCommandSlotNumbersForCarsWithRegistrationNumber(args);
            default -> String.format("Unknown command: %s", action);
        };
    }

    private String runCommandCreateParkingLot(List<String> args) {
        Integer size = Integer.parseInt(args.get(0));
        var newSlots = parkingServiceImpl.createParkingSlots(size);
        if (newSlots.isEmpty()) return "Invalid parking lot size";
        return String.format("Created a parking lot with %d slot(s)", newSlots.size());
    }

    private String runCommandPark(List<String> args) {
        var occupant = ParkingLotOccupant.builder()
                .registrationNumber(args.get(0))
                .colour(args.get(1))
                .build();
        return parkingServiceImpl.occupyAnyParkingLotSlot(occupant)
                .map(slot -> String.format("Allocated slot number: %d", slot.getSlotNumber()))
                .orElse("Sorry, parking lot is full");
    }

    private String runCommandLeave(List<String> args) {
        Integer slotNumber = Integer.parseInt(args.get(0));
        return parkingServiceImpl.removeParkingLotSlotOccupant(slotNumber)
                .map(occupant -> String.format("Slot number %d is free", slotNumber))
                .orElse(String.format("Slot number %d was already empty or does not exist", slotNumber));
    }

    private String runCommandStatus() {
        return parkingServiceImpl.getParkingLotStatus();
    }

    private String runCommandPlateNumbersForCarsWithColour(List<String> args) {
        String colour = args.get(0);
        var slots = parkingServiceImpl.getParkingLotSlotsFromOccupantColour(colour);
        if (slots.isEmpty()) return String.format("No occupants found with colour: %s", colour);
        return slots.stream()
                .map(ParkingLotSlot::getOccupant)
                .flatMap(Optional::stream)
                .map(ParkingLotOccupant::getRegistrationNumber)
                .collect(Collectors.joining(", "));
    }

    private String runCommandSlotNumbersForCarsWithColour(List<String> args) {
        String colour = args.get(0);
        var slots = parkingServiceImpl.getParkingLotSlotsFromOccupantColour(colour);
        if (slots.isEmpty()) return String.format("No occupants found with colour: %s", colour);
        return slots.stream()
                .map(slot -> String.valueOf(slot.getSlotNumber()))
                .collect(Collectors.joining(", "));
    }

    private String runCommandSlotNumbersForCarsWithRegistrationNumber(List<String> args) {
        String registrationNumber = args.get(0);
        return parkingServiceImpl.getParkingLotSlotFromOccupantPlateNumber(registrationNumber)
                .map(slot -> String.valueOf(slot.getSlotNumber()))
                .orElse(String.format("Plate number %s not found in parking lot", registrationNumber));
    }
}
