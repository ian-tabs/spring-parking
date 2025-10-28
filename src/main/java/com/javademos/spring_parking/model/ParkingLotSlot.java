package com.javademos.spring_parking.model;

import lombok.*;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParkingLotSlot {
    private Integer slotnumber;
    private ParkingLotOccupant occupant;

    public Optional<ParkingLotOccupant> getOccupant() {
        return Optional.ofNullable(occupant);
    }
}
