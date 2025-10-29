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

	ParkingLotOccupant newOccupant =  ParkingLotOccupant.builder()
			.platenumber("ABC-000")
			.color("White")
			.build();

	ParkingLotOccupant firstSlotOccupant =  ParkingLotOccupant.builder()
			.platenumber("ABC-111")
			.color("Blue")
			.build();

	ParkingLotOccupant secondSlotOccupant =  ParkingLotOccupant.builder()
			.platenumber("ABC-222")
			.color("Red")
			.build();

	ParkingLotOccupant thirdSlotOccupant =  ParkingLotOccupant.builder()
			.platenumber("ABC-333")
			.color("Green")
			.build();

	ParkingLotOccupant fourthSlotOccupant =  ParkingLotOccupant.builder()
			.platenumber("ABC-444")
			.color("Blue")
			.build();

	@BeforeEach
	void initializeTestData() {
		clearParkingLot();
	}

	void clearParkingLot() {
		parkingLot.clear();
	}

	void loadFullParkingLot() {
		parkingLot.addAll(List.of(
				ParkingLotSlot.builder().slotnumber(1).occupant(firstSlotOccupant).build(),
				ParkingLotSlot.builder().slotnumber(2).occupant(secondSlotOccupant).build(),
				ParkingLotSlot.builder().slotnumber(3).occupant(thirdSlotOccupant).build(),
				ParkingLotSlot.builder().slotnumber(4).occupant(fourthSlotOccupant).build()
		));
	}

	void loadGenericParkingLot() {
		parkingLot.addAll(List.of(
				ParkingLotSlot.builder().slotnumber(1).occupant(firstSlotOccupant).build(),
				ParkingLotSlot.builder().slotnumber(2).occupant(secondSlotOccupant).build(),
				ParkingLotSlot.builder().slotnumber(3).build(),
				ParkingLotSlot.builder().slotnumber(4).occupant(fourthSlotOccupant).build()
		));
	}

	@Test
	void createParkingLotSlotsSuccess() {
		Integer size = 6;
		parkingServiceImpl.createParkingSlots(size);
		assertEquals(size, Integer.valueOf(parkingLot.size()));
	}

	@Test
	void occupyParkingLotSlotSuccess() {
		loadGenericParkingLot();
		Integer slotNumber = 3;
		int parkingLotListIndex = 2;
		parkingServiceImpl.occupyParkingLotSlot(slotNumber, newOccupant);
		assertTrue(parkingLot.get(parkingLotListIndex).getOccupant().isPresent());
	}

	@Test
	void removeParkingLotOccupantSuccess() {
		loadFullParkingLot();
		Integer slotNumber = 1;

		Optional<ParkingLotOccupant> expectedOccupant = parkingLot.get(0).getOccupant();
		ParkingLotOccupant removedOccupant = parkingServiceImpl.removeParkingLotSlotOccupant(slotNumber);

		assertTrue(expectedOccupant.isPresent() &&
				expectedOccupant.get().equals(removedOccupant) &&
				parkingLot.get(0).getOccupant().isEmpty());
	}

	@Test
	void getParkingLotStatusSuccess() {
		loadGenericParkingLot();
		String expected = """
			Slot No. | Plate No | Colour
			1 | ABC-111 | Blue
			2 | ABC-222 | Red
			4 | ABC-444 | Blue
		""";
		String actual = parkingServiceImpl.getParkingLotStatus();
		assertEquals(expected.trim(), actual.trim());
	}

	@Test
	void getParkingLotSlotsFromOccupantColorSuccess() {
		loadGenericParkingLot();
		String colorQuery = "Blue";
		List<Integer> expectedParkingSlotNumbers = List.of(1,4);
		List<Integer> actualParkingSlotNumbers = parkingServiceImpl.getParkingLotSlotsFromOccupantColor(colorQuery)
				.stream()
				.map(ParkingLotSlot::getSlotnumber)
				.toList();
		assertEquals(expectedParkingSlotNumbers, actualParkingSlotNumbers);
	}

	@Test
	void getParkingLotSlotFromOccupantPlateNumberSuccess() {
		loadGenericParkingLot();
		Integer expectedSlotNumber = 1;
		String plateNumberQuery = "ABC-111";
		Optional<ParkingLotSlot> result = parkingServiceImpl.getParkingLotSlotFromOccupantPlateNumber(plateNumberQuery);
		assertTrue(result.isPresent() && expectedSlotNumber.equals(result.get().getSlotnumber()));
	}

}