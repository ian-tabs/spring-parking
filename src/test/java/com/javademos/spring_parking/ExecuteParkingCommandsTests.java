package com.javademos.spring_parking;

import com.javademos.spring_parking.model.ParkingLotOccupant;
import com.javademos.spring_parking.model.ParkingLotSlot;
import com.javademos.spring_parking.service.ParkingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class ExecuteParkingCommandsTests {

    @Mock
    private ParkingService parkingServiceMock;

    @InjectMocks
    private ExecuteParkingCommands executeParkingCommands;

    @BeforeEach
    void initializeMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCommandCreateParkingLotSuccess() {
        int validParkingLotSize = 6;
        when(parkingServiceMock.createParkingSlots(validParkingLotSize))
                .thenReturn(List.of(
                        ParkingLotSlot.builder().slotnumber(1).build(),
                        ParkingLotSlot.builder().slotnumber(2).build(),
                        ParkingLotSlot.builder().slotnumber(3).build(),
                        ParkingLotSlot.builder().slotnumber(4).build(),
                        ParkingLotSlot.builder().slotnumber(5).build(),
                        ParkingLotSlot.builder().slotnumber(6).build()
                ));
        String result = executeParkingCommands.getSystemResponseFromCommand(String.format("create_parking_lot %d",validParkingLotSize));
        assertEquals(String.format("Created a parking lot with %d slot(s)",validParkingLotSize), result);
    }

    @Test
    void testCommandCreateParkingLotFail() {
        int invalidParkingLotSize = -1;
        when(parkingServiceMock.createParkingSlots(invalidParkingLotSize))
                .thenReturn(Collections.emptyList());
        String result = executeParkingCommands.getSystemResponseFromCommand(String.format("create_parking_lot %d",invalidParkingLotSize));
        assertEquals("Invalid parking lot size", result);
    }

    @Test
    void testCommandParkSuccess() {
        String plateNumber = "ABC-1234";
        String colour = "White";
        ParkingLotSlot mockedOccupiedSlot = ParkingLotSlot.builder().slotnumber(1).build();
        when(parkingServiceMock.occupyAnyParkingLotSlot(any(ParkingLotOccupant.class)))
                .thenReturn(Optional.of(mockedOccupiedSlot));
        String result = executeParkingCommands.getSystemResponseFromCommand(
                String.format("park %s %s", plateNumber, colour)
        );
        assertEquals(String.format("Allocated slot number: %d", mockedOccupiedSlot.getSlotnumber()), result);
    }

    @Test
    void testCommandParkFail() {
        String plateNumber = "ABC-1234";
        String colour = "White";
        when(parkingServiceMock.occupyAnyParkingLotSlot(any(ParkingLotOccupant.class)))
                .thenReturn(Optional.empty());
        String result = executeParkingCommands.getSystemResponseFromCommand(
                String.format("park %s %s", plateNumber, colour)
        );
        assertEquals("Sorry, parking lot is full", result);
    }

    @Test
    void testCommandLeaveSuccess() {
        int slotNumber = 4;
        when(parkingServiceMock.removeParkingLotSlotOccupant(slotNumber))
                .thenReturn(Optional.of(ParkingLotOccupant.builder()
                        .platenumber("ABC-7777")
                        .colour("Red")
                        .build()));
        String result = executeParkingCommands.getSystemResponseFromCommand(String.format("leave %d", slotNumber));
        assertEquals(String.format("Slot number %d is free", slotNumber), result);
    }

    @Test
    void testCommandLeaveFail() {
        int slotNumber = 4;
        when(parkingServiceMock.removeParkingLotSlotOccupant(slotNumber))
                .thenReturn(Optional.empty());
        String result = executeParkingCommands.getSystemResponseFromCommand(String.format("leave %d", slotNumber));
        assertEquals(String.format("Slot number %d was already empty or does not exist", slotNumber), result);
    }

    @Test
    void testCommandStatus() {
        String mockedStatus = """
            Slot No. | Plate No | Colour
            1 | ABC-111 | Blue
            2 | ABC-222 | Red
            """;
        when(parkingServiceMock.getParkingLotStatus())
                .thenReturn(mockedStatus);
        String result = executeParkingCommands.getSystemResponseFromCommand("status");
        assertEquals(mockedStatus.trim(), result.trim());
    }

    @Test
    void testCommandPlateNumbersForCarsWithColourSuccess() {
        String presentColour = "White";
        var occupantOne = ParkingLotOccupant.builder()
                .platenumber("ABC-1234")
                .colour(presentColour)
                .build();
        var occupantTwo = ParkingLotOccupant.builder()
                .platenumber("ABC-9999")
                .colour(presentColour)
                .build();
        var occupantFour = ParkingLotOccupant.builder()
                .platenumber("ABC-333")
                .colour(presentColour)
                .build();
        List<ParkingLotSlot> slots = List.of(
                ParkingLotSlot.builder()
                        .slotnumber(1)
                        .occupant(occupantOne)
                        .build(),
                ParkingLotSlot.builder()
                        .slotnumber(2)
                        .occupant(occupantTwo)
                        .build(),
                ParkingLotSlot.builder()
                        .slotnumber(4)
                        .occupant(occupantFour)
                        .build()
        );
        when(parkingServiceMock.getParkingLotSlotsFromOccupantColour(presentColour))
                .thenReturn(slots);
        String result = executeParkingCommands.getSystemResponseFromCommand(String.format("plate_numbers_for_cars_with_colour %s",presentColour));
        assertEquals(String.format("%s, %s, %s",occupantOne.getPlatenumber(),occupantTwo.getPlatenumber(),occupantFour.getPlatenumber()), result);
    }

    @Test
    void testCommandPlateNumbersForCarsWithColourNoneFound() {
        String missingColour = "Green";
        when(parkingServiceMock.getParkingLotSlotsFromOccupantColour(missingColour))
                .thenReturn(Collections.emptyList());
        String result = executeParkingCommands.getSystemResponseFromCommand(String.format("plate_numbers_for_cars_with_colour %s",missingColour));
        assertEquals(String.format("No occupants found with colour: %s",missingColour), result);
    }

    @Test
    void testCommandSlotNumbersForCarsWithColourSuccess() {
        String presentColour = "White";
        var occupantOne = ParkingLotOccupant.builder()
                .platenumber("ABC-1234")
                .colour(presentColour)
                .build();
        var occupantTwo = ParkingLotOccupant.builder()
                .platenumber("ABC-9999")
                .colour(presentColour)
                .build();
        var occupantFour = ParkingLotOccupant.builder()
                .platenumber("ABC-333")
                .colour(presentColour)
                .build();
        List<ParkingLotSlot> slots = List.of(
                ParkingLotSlot.builder()
                        .slotnumber(1)
                        .occupant(occupantOne)
                        .build(),
                ParkingLotSlot.builder()
                        .slotnumber(2)
                        .occupant(occupantTwo)
                        .build(),
                ParkingLotSlot.builder()
                        .slotnumber(4)
                        .occupant(occupantFour)
                        .build()
        );
        when(parkingServiceMock.getParkingLotSlotsFromOccupantColour(presentColour))
                .thenReturn(slots);
        String result = executeParkingCommands.getSystemResponseFromCommand(
                String.format("slot_numbers_for_cars_with_colour %s", presentColour)
        );
        assertEquals(String.format("%d, %d, %d",slots.get(0).getSlotnumber(),slots.get(1).getSlotnumber(),slots.get(2).getSlotnumber()), result);
    }

    @Test
    void testCommandSlotNumbersForCarsWithColourNoneFound() {
        String missingColour = "Green";
        when(parkingServiceMock.getParkingLotSlotsFromOccupantColour(missingColour))
                .thenReturn(Collections.emptyList());
        String result = executeParkingCommands.getSystemResponseFromCommand(String.format("plate_numbers_for_cars_with_colour %s",missingColour));
        assertEquals(String.format("No occupants found with colour: %s",missingColour), result);
    }

    @Test
    void testCommandSlotNumberForRegistrationNumberSuccess() {
        String existingPlateNumber = "ABC-3141";
        var foundOccupant = ParkingLotOccupant.builder()
                .platenumber(existingPlateNumber)
                .build();
        int expectedSlotNumber = 6;
        when(parkingServiceMock.getParkingLotSlotFromOccupantPlateNumber(existingPlateNumber))
                .thenReturn(Optional.of(ParkingLotSlot.builder()
                        .slotnumber(expectedSlotNumber)
                        .occupant(foundOccupant)
                        .build()));
        String result = executeParkingCommands.getSystemResponseFromCommand(
                String.format("slot_number_for_registration_number %s", existingPlateNumber));
        assertEquals(String.valueOf(expectedSlotNumber), result);
    }

    @Test
    void testCommandSlotNumberForRegistrationNumberFail() {
        String nonExistentPlateNumber = "ABD-1111";
        when(parkingServiceMock.getParkingLotSlotFromOccupantPlateNumber(nonExistentPlateNumber))
                .thenReturn(Optional.empty());
        String result = executeParkingCommands.getSystemResponseFromCommand(
                String.format("slot_number_for_registration_number %s", nonExistentPlateNumber));
        assertEquals(String.format("Plate number %s not found in parking lot",nonExistentPlateNumber), result);
    }

}
