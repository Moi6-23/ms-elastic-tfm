package com.parking.parkingapp.service.parkings;

import com.parking.parkingapp.dto.ParkingsDto.ParkingOnly.ParkingOnlyRequestDto;
import com.parking.parkingapp.dto.ParkingsDto.ParkingOnly.ParkingOnlyResponseDto;
import com.parking.parkingapp.dto.ParkingsDto.Parkings.ParkingsQueryResponse;
import com.parking.parkingapp.dto.ParkingsDto.Parkings.ParkingsWithoutResponse;
import com.parking.parkingapp.dto.ParkingsDto.UpdateParking.ParkingUpdateRequestDto;
import com.parking.parkingapp.dto.SimpleResponse;

public interface ParkingsService {

	ParkingsQueryResponse getParkings();
	ParkingsWithoutResponse getParkingsWithout();
	ParkingOnlyResponseDto findPlaceWithSpotsOnFloor(ParkingOnlyRequestDto request);
	SimpleResponse updateSpotStatus(ParkingUpdateRequestDto request);
}
