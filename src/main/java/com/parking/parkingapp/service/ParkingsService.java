package com.parking.parkingapp.service;
import com.parking.parkingapp.controller.model.ParkingsQueryResponse;
import com.parking.parkingapp.controller.model.ParkingsWithoutResponse;

public interface ParkingsService {

	ParkingsQueryResponse getParkings();
	ParkingsWithoutResponse getParkingswithout();

}
