package com.parking.parkingapp.service.parkings;
import com.parking.parkingapp.dto.ParkingOnly.ParkingOnlyRequestDto;
import com.parking.parkingapp.dto.ParkingOnly.ParkingOnlyResponseDto;
import com.parking.parkingapp.dto.Parkings.ParkingsQueryResponse;
import com.parking.parkingapp.dto.Parkings.ParkingsWithoutResponse;

public interface ParkingsService {

	ParkingsQueryResponse getParkings();
	ParkingsWithoutResponse getParkingswithout();
	ParkingOnlyResponseDto findPlaceWithPlazasInPiso(ParkingOnlyRequestDto request);
}
