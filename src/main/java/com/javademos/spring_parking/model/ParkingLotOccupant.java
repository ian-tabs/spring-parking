package com.javademos.spring_parking.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParkingLotOccupant {
    private String registrationNumber;
    private String colour;
}
