package com.javademos.spring_parking.service;

import com.javademos.spring_parking.model.ParkingLotOccupant;
import com.javademos.spring_parking.model.ParkingLotSlot;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParkingServiceImpl implements ParkingService {

    @Override
    public void createParkingSlots(Integer size) {

    }

    @Override
    public void occupyParkingLotSlot(Integer parkingLotSlotNumber, ParkingLotOccupant occupant) {

    }

    @Override
    public ParkingLotOccupant removeParkingLotSlotOccupant(Integer parkingLotSlotNumber) {
        return null;
    }

    @Override
    public String getParkingLotStatus() {
        return "";
    }

    @Override
    public List<ParkingLotSlot> getParkingLotSlotsFromOccupantColor(String color) {
        return List.of();
    }

    @Override
    public Optional<ParkingLotSlot> getParkingLotSlotFromOccupantPlateNumber(String platenumber) {
        return Optional.empty();
    }
}
