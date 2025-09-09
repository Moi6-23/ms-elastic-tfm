package com.parking.parkingapp.controller;
import java.util.Map;

import com.parking.parkingapp.config.AdminGuard;
import com.parking.parkingapp.dto.ParkingsDto.ParkingOnly.ParkingOnlyRequestDto;
import com.parking.parkingapp.dto.ParkingsDto.ParkingOnly.ParkingOnlyResponseDto;
import com.parking.parkingapp.dto.ParkingsDto.Parkings.ParkingsQueryResponse;
import com.parking.parkingapp.dto.ParkingsDto.Parkings.ParkingsWithoutResponse;
import com.parking.parkingapp.dto.ParkingsDto.UpdateParking.ParkingUpdateRequestDto;
import com.parking.parkingapp.dto.SimpleResponse;
import com.parking.parkingapp.service.parkings.ParkingsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
	private final AdminGuard adminGuard;

	@GetMapping("/parkings")
	public ResponseEntity<?> getParkings(
			HttpServletRequest http,
			@RequestHeader Map<String, String> headers,
			@RequestParam(name = "excludeSpots", required = false, defaultValue = "false") boolean excludeSpots, HttpServletRequest httpServletRequest) {
		log.info("Headers: {}", headers);
		log.info("GET /parkings - excludeSpots: {}", excludeSpots);
		if (excludeSpots) {
			ParkingsWithoutResponse parkings = service.getParkingsWithout();
			// Si no hay nada, 204 No Content
			if (parkings == null || parkings.getParkings() == null || parkings.getParkings().isEmpty()) {
				return ResponseEntity.noContent().build();
			}
			return ResponseEntity.ok(parkings);
		} else {
			ParkingsQueryResponse parkings = service.getParkings();
			if (parkings == null || parkings.getParkings() == null || parkings.getParkings().isEmpty()) {
				return ResponseEntity.noContent().build();
			}
			return ResponseEntity.ok(parkings);
		}
	}

	@GetMapping("/parkings/{id}")
	public ResponseEntity<?> findPlaceWithPlazasInFloor(
			@PathVariable("id") String parkingId,
			@RequestParam(name = "floorNumber") Integer floorNumber) {

		log.info("GET /parkings/{}?floorNumber={} - start", parkingId, floorNumber);

		if (floorNumber == null || floorNumber < 1) {
			log.warn("Invalid floorNumber received: {}", floorNumber);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					new SimpleResponse(400, "El parámetro 'floorNumber' debe ser >= 1")
			);
		}
		if (parkingId == null || parkingId.isBlank()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					new SimpleResponse(400, "El 'parkingId' no puede ser nulo o vacío")
			);
		}

		ParkingOnlyRequestDto request = new ParkingOnlyRequestDto(parkingId, floorNumber);
		ParkingOnlyResponseDto response = service.findPlaceWithSpotsOnFloor(request);

		// Si el servicio devuelve null o no encuentra, responde 404
		if (response == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new SimpleResponse(404, "Parqueadero o piso no encontrado"));
		}

		log.info("GET /parkings/{} - end", parkingId);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/parkings/{parkingId}/spots/{spotId}")
	public ResponseEntity<SimpleResponse> updatePlazaStatus(
			HttpServletRequest http,
			@PathVariable("parkingId") String parkingId,
			@PathVariable("spotId") String spotId,
			@Valid @RequestBody ParkingUpdateRequestDto request) {

		// Si no pasa, AdminGuard debería lanzar 401/403 -> GlobalExceptionHandler responderá
		adminGuard.enforce(http);

		log.info("PATCH /parkings/{}/spots/{} - start", parkingId, spotId);

		if (parkingId == null || parkingId.isBlank()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new SimpleResponse(400, "El 'parkingId' no puede ser nulo o vacío"));
		}
		if (spotId == null || spotId.isBlank()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new SimpleResponse(400, "El 'spotId' no puede ser nulo o vacío"));
		}

		// Inyectar al DTO desde el path
		request.setParkingId(parkingId);
		request.setSpotId(spotId);

		// el servicio debe devolver un SimpleResponse con código real (200/400/404/409…)
		SimpleResponse result = service.updateSpotStatus(request);

		// Usa el código del servicio como HTTP real
		HttpStatus status = switch (result.getCode()) {
			case 200 -> HttpStatus.OK;
			case 201 -> HttpStatus.CREATED;
			case 204 -> HttpStatus.NO_CONTENT;
			case 400 -> HttpStatus.BAD_REQUEST;
			case 401 -> HttpStatus.UNAUTHORIZED;
			case 403 -> HttpStatus.FORBIDDEN;
			case 404 -> HttpStatus.NOT_FOUND;
			case 409 -> HttpStatus.CONFLICT;
			default  -> HttpStatus.OK; 
		};

		log.info("PATCH /parkings/{}/spots/{} - end -> {}", parkingId, spotId, status.value());
		return ResponseEntity.status(status).body(result);
	}
}
