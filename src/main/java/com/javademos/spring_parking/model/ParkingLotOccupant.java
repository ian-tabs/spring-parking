package com.javademos.spring_parking.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParkingLotOccupant {
    private String platenumber;
    private String color;

}
