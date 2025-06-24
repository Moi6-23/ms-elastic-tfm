package com.parking.parkingapp.dto.ParkingsDto.ParkingOnly;

import com.parking.parkingapp.data.model.Places;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingOnlyResponseDto {
    private int code;
    private List<Places> data;
}
