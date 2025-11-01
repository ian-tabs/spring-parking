package com.javademos.spring_parking.service;

import com.javademos.spring_parking.model.ParkingLotOccupant;
import com.javademos.spring_parking.model.ParkingLotSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ParkingServiceCommandLineImpl implements ParkingService {

    @Autowired
    List<ParkingLotSlot> parkingLot;

    @Override
    public List<ParkingLotSlot> createParkingSlots(Integer size) {
        if (size == null || size < 0) {
            return Collections.emptyList();
        }
        List<ParkingLotSlot> newSlots = new ArrayList<>();
        for (int slotNumber = 1; slotNumber <= size; slotNumber++) {
            ParkingLotSlot slot = ParkingLotSlot.builder()
                    .slotNumber(slotNumber)
                    .build();
            parkingLot.add(slot);
            newSlots.add(slot);
        }
        return newSlots;
    }

    @Override
    public Optional<ParkingLotSlot> occupyAnyParkingLotSlot(ParkingLotOccupant occupant) {
        return findFirstAvailableParkingSlot().map(slot -> {
            slot.setOccupant(occupant);
            return slot;
        });
    }

    @Override
    public Optional<ParkingLotSlot> occupySpecificParkingLotSlot(Integer parkingLotSlotNumber, ParkingLotOccupant occupant) {
        return getSlotIfAvailable(parkingLotSlotNumber).map(slot -> {
            slot.setOccupant(occupant);
            return slot;
        });
    }

    @Override
    public Optional<ParkingLotOccupant> removeParkingLotSlotOccupant(Integer slotNumber) {
        return getParkingLotSlotFromSlotNumber(slotNumber)
                        .flatMap(slot -> slot.getOccupant().map(occupant -> {
                            slot.setOccupant(null);
                            return occupant;
                        }));
    }

    @Override
    public String getParkingLotStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("Slot No. | Plate No | Colour\n");
        parkingLot.stream()
                .filter(slot -> slot.getOccupant().isPresent())
                .forEach(slot -> {
                    ParkingLotOccupant occupant = slot.getOccupant().get();
                    sb.append(String.format("\t%d | %s | %s\n",
                            slot.getSlotNumber(),
                            occupant.getRegistrationNumber(),
                            occupant.getColour()));
                });
        return sb.toString().trim();
    }

    @Override
    public List<ParkingLotSlot> getParkingLotSlotsFromOccupantColour(String color) {
        return parkingLot.stream()
                .filter(slot -> slot.getOccupant()
                        .map(occupant -> occupant.getColour().equalsIgnoreCase(color))
                        .orElse(false))
                .toList();
    }

    @Override
    public Optional<ParkingLotSlot> getParkingLotSlotFromOccupantPlateNumber(String platenumber) {
        return parkingLot.stream()
                .filter(slot -> slot.getOccupant()
                        .map(occupant -> occupant.getRegistrationNumber().equalsIgnoreCase(platenumber))
                        .orElse(false))
                .findFirst();
    }

    private Optional<ParkingLotSlot> findFirstAvailableParkingSlot() {
        return parkingLot.stream()
                .filter(slot -> slot.getOccupant().isEmpty())
                .findFirst();
    }

    private Optional<ParkingLotSlot> getSlotIfAvailable(Integer slotNumber) {
        return getParkingLotSlotFromSlotNumber(slotNumber)
                .filter(slot -> slot.getOccupant().isEmpty());
    }

    private Optional<ParkingLotSlot> getParkingLotSlotFromSlotNumber(Integer slotnumber) {
        boolean isInvalidSlotNumber = slotnumber == null || slotnumber < 1 || slotnumber > parkingLot.size();
        if (isInvalidSlotNumber) {
            return Optional.empty();
        }
        return Optional.ofNullable(parkingLot.get(slotnumber - 1));
    }

}
