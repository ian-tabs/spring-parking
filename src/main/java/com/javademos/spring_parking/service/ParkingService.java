package com.javademos.spring_parking.service;

import com.javademos.spring_parking.model.ParkingLotOccupant;
import com.javademos.spring_parking.model.ParkingLotSlot;

import java.util.List;
import java.util.Optional;

public interface ParkingService {
     Optional<List<ParkingLotSlot>> createParkingSlots(Integer size);
     Optional<ParkingLotSlot> occupyAnyParkingLotSlot(ParkingLotOccupant occupant);
     Optional<ParkingLotSlot> occupySpecificParkingLotSlot(Integer parkingLotSlotNumber, ParkingLotOccupant occupant);
     Optional<ParkingLotOccupant> removeParkingLotSlotOccupant(Integer parkingLotSlotNumber);
     String getParkingLotStatus();
     List<ParkingLotSlot> getParkingLotSlotsFromOccupantColour(String color);
     Optional<ParkingLotSlot> getParkingLotSlotFromOccupantPlateNumber(String platenumber);
}