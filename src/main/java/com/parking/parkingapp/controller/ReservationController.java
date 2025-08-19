package com.parking.parkingapp.controller;
import com.parking.parkingapp.dto.Reservas.CancelarReserva.CancelReservationRequest;
import com.parking.parkingapp.dto.Reservas.ConsultaReservas.ReservationByUserRequestDto;
import com.parking.parkingapp.dto.Reservas.ConsultaReservas.SearchReservationByUserResponse;
import com.parking.parkingapp.dto.Reservas.ConsultaReservas.SearchReservationResponse;
import com.parking.parkingapp.dto.Reservas.ReservarPlaza.ReservationRequest;
import com.parking.parkingapp.dto.Reservas.ReservarPlaza.ReservationResponse;
import com.parking.parkingapp.dto.SimpleResponse;
import com.parking.parkingapp.service.reservation.ReservationServiceImpl;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Validated
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationServiceImpl reservationService;

    @PostMapping("/parkings/reservations")
    public ResponseEntity<ReservationResponse> reservePlace(@RequestBody ReservationRequest request) {
        log.debug("Reservation request received: {}", request);
        ReservationResponse response = reservationService.makeReservation(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reservations")
    public ResponseEntity<SearchReservationResponse> getReservation() {
        log.info("GET /reservations - start");
        SearchReservationResponse reservations = reservationService.getAllReservations();
        log.info("GET /reservations - success");
        return ResponseEntity.ok(reservations);
    }

    @PatchMapping("/reservations/{id}")
    public ResponseEntity<SimpleResponse> cancelReservation(
            @PathVariable("id") String reservationId,
            @RequestBody CancelReservationRequest request) {

        log.debug("PATCH /reservations/{} - cancel request: {}", reservationId, request);

        request.setReservationId(reservationId);

        SimpleResponse response = reservationService.cancelReservation(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reservations/by-user")
    public ResponseEntity<?> getReservationsByUser(
            @RequestParam @NotBlank @Email String email
    ) {
        log.info("GET /reservations/by-user - email={}", email);

        var request = ReservationByUserRequestDto.builder()
                .email(email)
                .build();

        var response = reservationService.getReservationsByUser(request);

        log.info("GET /reservations/by-user - success - found={}",
                response.getReservations() != null ? response.getReservations().size() : 0);

        if (response.getReservations() == null || response.getReservations().isEmpty()) {
            Map<String, Object> body = Map.of(
                    "code", 404,
                    "message", "No se encontraron reservas para el email: " + email
            );
            return ResponseEntity.status(404).body(body);        }
        return ResponseEntity.ok(response);
    }
}
