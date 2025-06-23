package com.parking.parkingapp.controller;
import java.util.Map;
import com.parking.parkingapp.controller.model.ParkingsQueryResponse;
import com.parking.parkingapp.service.ParkingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

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

}
