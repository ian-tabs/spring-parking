package com.javademos.spring_parking.service;

import com.javademos.spring_parking.model.ParkingLotOccupant;
import com.javademos.spring_parking.model.ParkingLotSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParkingServiceImpl implements ParkingService {

    @Autowired
    List<ParkingLotSlot> parkingLot;

    @Override
    public void createParkingSlots(Integer size) {
        for (int slotnumber = 1; slotnumber <= size; slotnumber++) {
            parkingLot.add(ParkingLotSlot.builder()
                    .slotnumber(slotnumber)
                    .build());
        }
    }

    @Override
    public void occupyParkingLotSlot(Integer parkingLotSlotNumber, ParkingLotOccupant occupant) {
        findAvailableSlot(parkingLotSlotNumber)
                .ifPresent(slot -> slot.setOccupant(occupant));
    }

    @Override
    public ParkingLotOccupant removeParkingLotSlotOccupant(Integer slotNumber) {
        var parkingLotSlotOptional = getParkingLotSlotFromSlotNumber(slotNumber);
        if (parkingLotSlotOptional.isEmpty()) {
            return null;
        }
        var parkingSlot = parkingLotSlotOptional.get();
        var occupantOptional = parkingSlot.getOccupant();
        if (occupantOptional.isEmpty()) {
            return null;
        }
        var occupant = occupantOptional.get();
        parkingSlot.setOccupant(null);
        return occupant;
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
                            slot.getSlotnumber(),
                            occupant.getPlatenumber(),
                            occupant.getColor()));
                });
        return sb.toString().trim();
    }

    @Override
    public List<ParkingLotSlot> getParkingLotSlotsFromOccupantColor(String color) {
        return parkingLot.stream()
                .filter(slot -> slot.getOccupant()
                        .map(occupant -> occupant.getColor().equalsIgnoreCase(color))
                        .orElse(false))
                .toList();
    }

    @Override
    public Optional<ParkingLotSlot> getParkingLotSlotFromOccupantPlateNumber(String platenumber) {
        return parkingLot.stream()
                .filter(slot -> slot.getOccupant()
                        .map(occupant -> occupant.getPlatenumber().equalsIgnoreCase(platenumber))
                        .orElse(false))
                .findFirst();
    }

    private Optional<ParkingLotSlot> findAvailableSlot(Integer slotNumber) {
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
