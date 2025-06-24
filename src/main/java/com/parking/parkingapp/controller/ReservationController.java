package com.parking.parkingapp.controller;

import com.parking.parkingapp.dto.ParkingsDto.Parkings.ParkingsWithoutResponse;
import com.parking.parkingapp.dto.Reservas.ConsultaReservas.SearchReservationResponse;
import com.parking.parkingapp.dto.Reservas.ReservarPlaza.ReservationRequest;
import com.parking.parkingapp.dto.Reservas.ReservarPlaza.ReservationResponse;
import com.parking.parkingapp.service.reservation.ReservationServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationServiceImpl reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> reservePlace(@RequestBody ReservationRequest request) {
        log.debug("Petición recibida para reservar plaza: {}", request);
        ReservationResponse response = reservationService.reserve(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<SearchReservationResponse> getReservation() {
        log.info("GET /reservations - inicio");
        SearchReservationResponse parkings = reservationService.getReservationAll();
        log.info("GET /reservations - éxito");
        return ResponseEntity.ok(parkings);
    }}
