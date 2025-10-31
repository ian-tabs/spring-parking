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
			.colour("White")
			.build();

	ParkingLotOccupant firstSlotOccupant =  ParkingLotOccupant.builder()
			.platenumber("ABC-111")
			.colour("Blue")
			.build();

	ParkingLotOccupant secondSlotOccupant =  ParkingLotOccupant.builder()
			.platenumber("ABC-222")
			.colour("Red")
			.build();

	ParkingLotOccupant thirdSlotOccupant =  ParkingLotOccupant.builder()
			.platenumber("ABC-333")
			.colour("Green")
			.build();

	ParkingLotOccupant fourthSlotOccupant =  ParkingLotOccupant.builder()
			.platenumber("ABC-444")
			.colour("Blue")
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
		List<ParkingLotSlot> createdSlots = parkingServiceImpl.createParkingSlots(size);

		assertNotNull(createdSlots);
		assertEquals(size, parkingLot.size());
		assertEquals(size, createdSlots.size());
	}


	@Test
	void occupyAnyParkingLotSlotSuccess() {
		loadGenericParkingLot();
		Optional<ParkingLotSlot> occupiedSlot = parkingServiceImpl.occupyAnyParkingLotSlot(newOccupant);
		assertTrue(occupiedSlot.isPresent()
						&& occupiedSlot.get().getOccupant().isPresent());
	}


	@Test
	void occupySpecificParkingLotSlotSuccess() {
		loadGenericParkingLot();
		Integer slotNumber = 3;
		Optional<ParkingLotSlot> occupiedSlot = parkingServiceImpl.occupySpecificParkingLotSlot(slotNumber, newOccupant);
		assertTrue(occupiedSlot.isPresent()
						&& occupiedSlot.get().getOccupant().isPresent()
						&& occupiedSlot.get().getSlotnumber().equals(slotNumber));
	}

	@Test
	void removeParkingLotOccupantSuccess() {
		loadFullParkingLot();
		int slotNumber = 1;
		int expectedRemovedOccupantParkingLotIndex = 0;
		Optional<ParkingLotOccupant> expectedRemovedOccupant = parkingLot.get(expectedRemovedOccupantParkingLotIndex).getOccupant();
		Optional<ParkingLotOccupant> removedOccupant = parkingServiceImpl.removeParkingLotSlotOccupant(slotNumber);
		assertTrue(removedOccupant.isPresent()
						&& expectedRemovedOccupant.isPresent()
						&& expectedRemovedOccupant.equals(removedOccupant)
						&& parkingLot.get(expectedRemovedOccupantParkingLotIndex).getOccupant().isEmpty());
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
		System.out.println(actual);
	}

	@Test
	void getParkingLotSlotsFromOccupantColorSuccess() {
		loadGenericParkingLot();
		String colorQuery = "Blue";
		List<Integer> expectedParkingSlotNumbers = List.of(1,4);
		List<Integer> actualParkingSlotNumbers = parkingServiceImpl.getParkingLotSlotsFromOccupantColour(colorQuery)
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