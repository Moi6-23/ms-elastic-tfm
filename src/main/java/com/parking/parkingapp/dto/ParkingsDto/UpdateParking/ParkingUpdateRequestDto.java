package com.parking.parkingapp.dto.ParkingsDto.UpdateParking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingUpdateRequestDto {
    private Boolean status;
    private Integer floorNumber;
    private String parkingId;
    private String spotId;
}
