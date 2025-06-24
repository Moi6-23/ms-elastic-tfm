package com.parking.parkingapp.controller;
import java.util.Map;
import com.parking.parkingapp.dto.ParkingsDto.ParkingOnly.ParkingOnlyRequestDto;
import com.parking.parkingapp.dto.ParkingsDto.ParkingOnly.ParkingOnlyResponseDto;
import com.parking.parkingapp.dto.ParkingsDto.Parkings.ParkingsQueryResponse;
import com.parking.parkingapp.dto.ParkingsDto.Parkings.ParkingsWithoutResponse;
import com.parking.parkingapp.dto.ParkingsDto.UpdateParking.ParkingUpdateRequestDto;
import com.parking.parkingapp.dto.SimpleResponse;
import com.parking.parkingapp.service.parkings.ParkingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ParkingController {

	private final ParkingsService service;

	@GetMapping("/places")
	public ResponseEntity<ParkingsQueryResponse> getParkings(
			@RequestHeader Map<String, String> headers) {
		log.info("Headers: {}", headers);
		log.info("GET /places - start");

		ParkingsQueryResponse parkings = service.getParkings();
		return ResponseEntity.ok(parkings);
	}

	@GetMapping("/places/out")
	public ResponseEntity<ParkingsWithoutResponse> getParkingsWithout(
			@RequestHeader Map<String, String> headers) {
		log.info("GET /placesout - start");
		ParkingsWithoutResponse parkings = service.getParkingsWithout();
		log.info("GET /placesout - success");
		return ResponseEntity.ok(parkings);
	}

	@PostMapping("/places/parking")
	public ResponseEntity<ParkingOnlyResponseDto> findPlaceWithPlazasInFloor(
			@RequestBody ParkingOnlyRequestDto request) {
		log.info("POST /places/parking - start");
		ParkingOnlyResponseDto resp = service.findPlaceWithSpotsOnFloor(request);
		log.info("POST /places/parking - end");
		return ResponseEntity.ok(resp);
	}

	@PostMapping("/places/update")
	public ResponseEntity<SimpleResponse> updatePlazaStatus(
			@RequestBody ParkingUpdateRequestDto request) {
		log.info("POST /places/update - start");
		SimpleResponse resp = service.updateSpotStatus(request);
		log.info("POST /places/update - end");
		return ResponseEntity.ok(resp);
	}
}
