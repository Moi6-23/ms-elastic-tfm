package com.parking.parkingapp.controller;

import com.parking.parkingapp.dto.ReservarPlaza.ReservationRequest;
import com.parking.parkingapp.dto.ReservarPlaza.ReservationResponse;
import com.parking.parkingapp.service.reservation.ReservationServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
