package com.parking.parkingapp.dto.ParkingOnly;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingOnlyRequestDto {
    private String parkingId;
    private int piso;
}
