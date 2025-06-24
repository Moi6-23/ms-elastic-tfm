package com.parking.parkingapp.controller;
import java.util.Map;

import com.parking.parkingapp.dto.ParkingOnly.ParkingOnlyRequestDto;
import com.parking.parkingapp.dto.ParkingOnly.ParkingOnlyResponseDto;
import com.parking.parkingapp.dto.Parkings.ParkingsQueryResponse;
import com.parking.parkingapp.dto.Parkings.ParkingsWithoutResponse;
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
		log.info("headers: {}", headers);
		log.info("LLamado ruta /places");

		ParkingsQueryResponse parkings = service.getParkings();
		return ResponseEntity.ok(parkings);
	}

	@GetMapping("/placesout")
	public ResponseEntity<ParkingsWithoutResponse> getParkingsWithout(
			@RequestHeader Map<String, String> headers) {
		log.info("GET /placesout - inicio");
		ParkingsWithoutResponse parkings = service.getParkingswithout();
		log.info("GET /placesout - éxito");
		return ResponseEntity.ok(parkings);
	}

	@PostMapping("/places/parking")
	public ResponseEntity<ParkingOnlyResponseDto> findPlaceWithPlazasInPiso(
			@RequestBody ParkingOnlyRequestDto request) {
		log.info("POST /places/parking - inicio");
		ParkingOnlyResponseDto resp = service.findPlaceWithPlazasInPiso(request);
		log.info("POST /places/parking - fin");
		return ResponseEntity.ok(resp);
	}
}
