package com.javademos.spring_parking.service;

import com.javademos.spring_parking.model.ParkingLotOccupant;
import com.javademos.spring_parking.model.ParkingLotSlot;

import java.util.List;
import java.util.Optional;

// TODO: separate create parking slots functionality from adding slots to parking lot functionality
public interface ParkingService {
     List<ParkingLotSlot> createParkingSlots(Integer size);
     Optional<ParkingLotSlot> occupyAnyParkingLotSlot(ParkingLotOccupant occupant);
     Optional<ParkingLotSlot> occupySpecificParkingLotSlot(Integer parkingLotSlotNumber, ParkingLotOccupant occupant);
     Optional<ParkingLotOccupant> removeParkingLotSlotOccupant(Integer parkingLotSlotNumber);
     String getParkingLotStatus();
     List<ParkingLotSlot> getParkingLotSlotsFromOccupantColour(String color);
     Optional<ParkingLotSlot> getParkingLotSlotFromOccupantPlateNumber(String platenumber);
     List<ParkingLotSlot> findEmptyParkingSlots();
     void removeParkingLotSlots(List<Integer> slotNumbers);
}