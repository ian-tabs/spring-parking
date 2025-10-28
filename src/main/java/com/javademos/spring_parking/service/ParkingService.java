package com.javademos.spring_parking.service;

import com.javademos.spring_parking.model.ParkingLotOccupant;
import com.javademos.spring_parking.model.ParkingLotSlot;

import java.util.List;
import java.util.Optional;

public interface ParkingService {
     void createParkingSlots(Integer size);
     void occupyParkingLotSlot(Integer parkingLotSlotNumber, ParkingLotOccupant occupant);
     ParkingLotOccupant removeParkingLotSlotOccupant(Integer parkingLotSlotNumber);
     String getParkingLotStatus();
     List<ParkingLotSlot> getParkingLotSlotsFromOccupantColor(String color);
     Optional<ParkingLotSlot> getParkingLotSlotFromOccupantPlateNumber(String platenumber);
}