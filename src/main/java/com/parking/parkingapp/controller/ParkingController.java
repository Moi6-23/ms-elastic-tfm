package com.parking.parkingapp.controller;
import java.util.Map;
import com.parking.parkingapp.dto.ParkingsDto.ParkingOnly.ParkingOnlyRequestDto;
import com.parking.parkingapp.dto.ParkingsDto.ParkingOnly.ParkingOnlyResponseDto;
import com.parking.parkingapp.dto.ParkingsDto.Parkings.ParkingsQueryResponse;
import com.parking.parkingapp.dto.ParkingsDto.Parkings.ParkingsWithoutResponse;
import com.parking.parkingapp.dto.ParkingsDto.UpdateParking.ParkingUpdateRequestDto;
import com.parking.parkingapp.dto.SimpleResponse;
import com.parking.parkingapp.service.parkings.ParkingsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ParkingController {

	private final ParkingsService service;

	@GetMapping("/parkings")
	public ResponseEntity<?> getParkings(
			@RequestHeader Map<String, String> headers,
			@RequestParam(name = "excludeSpots", required = false, defaultValue = "false") boolean excludeSpots) {

		log.info("Headers: {}", headers);
		log.info("GET /parkings - excludeSpots: {}", excludeSpots);

		if (excludeSpots) {
			ParkingsWithoutResponse parkings = service.getParkingsWithout();
			return ResponseEntity.ok(parkings);
		} else {
			ParkingsQueryResponse parkings = service.getParkings();
			return ResponseEntity.ok(parkings);
		}
	}

	@GetMapping("/parkings/{id}")
	public ResponseEntity<?> findPlaceWithPlazasInFloor(
			@PathVariable("id") String parkingId,
			@RequestParam(name = "floorNumber", required = true) Integer floorNumber) {

		log.info("GET /parkings/{}?floorNumber={} - start", parkingId, floorNumber);

		if (floorNumber == null || floorNumber < 1) {
			log.warn("Invalid floorNumber received: {}", floorNumber);
			return ResponseEntity
					.badRequest()
					.body(Map.of(
							"error", "Parámetro inválido",
							"message", "El parámetro 'floorNumber' debe ser un número entero mayor o igual a 1.",
							"status", 400
					));
		}

		ParkingOnlyRequestDto request = new ParkingOnlyRequestDto(parkingId, floorNumber);
		ParkingOnlyResponseDto response = service.findPlaceWithSpotsOnFloor(request);

		log.info("GET /parkings/{} - end", parkingId);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/parkings/{parkingId}/spots/{spotId}")
	public ResponseEntity<?> updatePlazaStatus(
			@PathVariable("parkingId") String parkingId,
			@PathVariable("spotId") String spotId,
			@Valid @RequestBody ParkingUpdateRequestDto request) {

		log.info("PATCH /parkings/{}/spots/{} - start", parkingId, spotId);

		// Validaciones manuales de path variables
		if (parkingId == null || parkingId.isBlank()) {
			return ResponseEntity.badRequest().body(Map.of(
					"status", 400,
					"error", "El 'parkingId' no puede ser nulo o vacío."
			));
		}

		if (spotId == null || spotId.isBlank()) {
			return ResponseEntity.badRequest().body(Map.of(
					"status", 400,
					"error", "El 'spotId' no puede ser nulo o vacío."
			));
		}

		// Inyectar al DTO desde path
		request.setParkingId(parkingId);
		request.setSpotId(spotId);

		SimpleResponse response = service.updateSpotStatus(request);

		log.info("PATCH /parkings/{}/spots/{} - end", parkingId, spotId);
		return ResponseEntity.ok(response);
	}
}
