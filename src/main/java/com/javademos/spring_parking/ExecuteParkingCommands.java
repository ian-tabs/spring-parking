package com.javademos.spring_parking;

import com.javademos.spring_parking.model.ParkingLotOccupant;
import com.javademos.spring_parking.model.ParkingLotSlot;
import com.javademos.spring_parking.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ExecuteParkingCommands implements CommandLineRunner {

    @Autowired
    ParkingService parkingServiceImpl;

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

    public String getSystemResponseFromCommand (String commandLine) {
        String REGEX_PATTERN_MULTI_WHITESPACE = "\\s+";
        List<String> splitCommandLine = new ArrayList<>(Arrays.asList(commandLine.split(REGEX_PATTERN_MULTI_WHITESPACE)));
        String commandAction = splitCommandLine.get(0);
        List<String> commandArgs = splitCommandLine.subList(1, splitCommandLine.size());
        String systemResponse;
        switch (commandAction) {
            case "create_parking_lot" -> systemResponse = runCommandCreateParkingLot(commandArgs);
            case "park" -> systemResponse = runCommandPark(commandArgs);
            case "leave" -> systemResponse = runCommandLeave(commandArgs);
            case "status" -> systemResponse = runCommandStatus();
            case "plate_numbers_for_cars_with_colour" -> systemResponse = runCommandPlateNumbersForCarsWithColour(commandArgs);
            case "slot_numbers_for_cars_with_colour" -> systemResponse = runCommandSlotNumbersForCarsWithColour(commandArgs);
            case "slot_number_for_registration_number" -> systemResponse = runCommandSlotNumbersForCarsWithRegistrationNumber(commandArgs);
            default -> systemResponse = "Unknown command: "+commandAction;
        }
        return systemResponse;
    }

    private String runCommandCreateParkingLot(List<String> commandArgs) {
        Integer size = Integer.parseInt(commandArgs.get(0));
        var newParkingSlots = parkingServiceImpl.createParkingSlots(size);
        if (newParkingSlots.isEmpty()) {
            return "Invalid parking lot size";
        }
        return String.format("Created a parking lot with %d slot(s)", newParkingSlots.size());
    }

    private String runCommandPark(List<String> commandArgs) {
        int COMMAND_PARK_PLATE_NUMBER_INDEX = 0;
        int COMMAND_PARK_COLOUR_INDEX = 1;
        var occupant = ParkingLotOccupant.builder()
                .registrationNumber(commandArgs.get(COMMAND_PARK_PLATE_NUMBER_INDEX))
                .colour(commandArgs.get(COMMAND_PARK_COLOUR_INDEX))
                .build();
        var occupiedSlotOptional = parkingServiceImpl.occupyAnyParkingLotSlot(occupant);
        return occupiedSlotOptional
                .map(slot -> String.format("Allocated slot number: %d", slot.getSlotNumber()))
                .orElse("Sorry, parking lot is full");
    }

    private String runCommandLeave(List<String> commandArgs) {
        int COMMAND_LEAVE_SLOT_NUMBER_INDEX = 0;
        Integer slotNumber = Integer.parseInt(commandArgs.get(COMMAND_LEAVE_SLOT_NUMBER_INDEX));
        var removedOccupantOptional = parkingServiceImpl.removeParkingLotSlotOccupant(slotNumber);
        return removedOccupantOptional
                .map(occupant -> String.format("Slot number %d is free", slotNumber))
                .orElse(String.format("Slot number %d was already empty or does not exist", slotNumber));
    }

    private String runCommandStatus() {
        return parkingServiceImpl.getParkingLotStatus();
    }

    private String runCommandPlateNumbersForCarsWithColour(List<String> commandArgs) {
        int COMMAND_PLATE_NUMBERS_FOR_CARS_WITH_COLOUR_COLOUR_INDEX = 0;
        String colour = commandArgs.get(COMMAND_PLATE_NUMBERS_FOR_CARS_WITH_COLOUR_COLOUR_INDEX);
        var slots = parkingServiceImpl.getParkingLotSlotsFromOccupantColour(colour);
        if (slots.isEmpty()) {
            return String.format("No occupants found with colour: %s",colour);
        }
        return slots.stream()
                .map(ParkingLotSlot::getOccupant)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(ParkingLotOccupant::getRegistrationNumber)
                .collect(Collectors.joining(", "));
    }

    private String runCommandSlotNumbersForCarsWithColour(List<String> commandArgs) {
        int COMMAND_SLOT_NUMBERS_FOR_CARS_WITH_COLOUR_COLOUR_INDEX = 0;
        String colour = commandArgs.get(COMMAND_SLOT_NUMBERS_FOR_CARS_WITH_COLOUR_COLOUR_INDEX);
        var slots = parkingServiceImpl.getParkingLotSlotsFromOccupantColour(colour);
        if (slots.isEmpty()) {
            return String.format("No occupants found with colour: %s",colour);
        }
        return slots.stream()
                .map(slot -> String.valueOf(slot.getSlotNumber()))
                .collect(Collectors.joining(", "));
    }

    private String runCommandSlotNumbersForCarsWithRegistrationNumber(List<String> commandArgs) {
        int COMMAND_SLOT_NUMBERS_FOR_CARS_WITH_REGISTRATION_NUMBER_REGISTRATION_NUMBER_INDEX = 0;
        String registrationNumber = commandArgs.get(COMMAND_SLOT_NUMBERS_FOR_CARS_WITH_REGISTRATION_NUMBER_REGISTRATION_NUMBER_INDEX);
        var slotOptional = parkingServiceImpl.getParkingLotSlotFromOccupantPlateNumber(registrationNumber);
        return slotOptional
                .map(slot -> String.valueOf(slot.getSlotNumber()))
                .orElse(String.format("Plate number %s not found in parking lot", registrationNumber));
    }


}
