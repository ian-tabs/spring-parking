package com.javademos.spring_parking;

import com.javademos.spring_parking.model.ParkingLotOccupant;
import com.javademos.spring_parking.model.ParkingLotSlot;
import com.javademos.spring_parking.service.ParkingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class SpringParkingApplicationTests {

	@Autowired
	List<ParkingLotSlot> parkingLot;

	@Autowired
	ParkingService parkingServiceImpl;

	@BeforeEach
	void initializeTestData() {
		clearParkingLot();
	}

	void clearParkingLot() {
		parkingLot.clear();
	}

	@Test
	void createParkingLotSlotsSuccess() {
		Integer size = 6;
		parkingServiceImpl.createParkingSlots(size);
        assertEquals((int) size, parkingLot.size());
	}

	@Test
	void occupyParkingLotSlotSuccess() {

	}

	@Test
	void removeParkingLotOccupantSuccess() {

	}

	@Test
	void getParkingLotStatusSuccess() {

	}

	@Test
	void getParkingLotSlotsFromOccupantColorSuccess() {

	}

	@Test
	void getParkingLotSlotFromOccupantPlateNumberSuccess() {

	}

}