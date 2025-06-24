package com.parking.parkingapp.service.parkings;
import com.parking.parkingapp.dto.ParkingsDto.ParkingOnly.ParkingOnlyRequestDto;
import com.parking.parkingapp.dto.ParkingsDto.ParkingOnly.ParkingOnlyResponseDto;
import com.parking.parkingapp.dto.ParkingsDto.Parkings.ParkingsQueryResponse;
import com.parking.parkingapp.dto.ParkingsDto.Parkings.ParkingsWithoutResponse;

public interface ParkingsService {

	ParkingsQueryResponse getParkings();
	ParkingsWithoutResponse getParkingswithout();
	ParkingOnlyResponseDto findPlaceWithPlazasInPiso(ParkingOnlyRequestDto request);
}
