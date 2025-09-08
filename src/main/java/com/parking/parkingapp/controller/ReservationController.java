package com.parking.parkingapp.controller;
import com.parking.parkingapp.dto.Reservas.CancelarReserva.CancelReservationRequest;
import com.parking.parkingapp.dto.Reservas.ConsultaReservas.ReservationByUserRequestDto;
import com.parking.parkingapp.dto.Reservas.ConsultaReservas.SearchReservationByUserResponse;
import com.parking.parkingapp.dto.Reservas.ConsultaReservas.SearchReservationResponse;
import com.parking.parkingapp.dto.Reservas.ReservarPlaza.ReservationRequest;
import com.parking.parkingapp.dto.Reservas.ReservarPlaza.ReservationResponse;
import com.parking.parkingapp.dto.SimpleResponse;
import com.parking.parkingapp.service.reservation.ReservationServiceImpl;
import com.parking.parkingapp.utils.SecurityUtils;
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

    @PostMapping("/parkings/{parkingId}/reservations")
    public ResponseEntity<?> reservePlace(
            @PathVariable("parkingId") String parkingId,
            @RequestBody ReservationRequest request
    ) {
        log.info("PATCH /parkings/{}/reservations - start", parkingId);

        // Validaciones manuales de path variables
        if (parkingId == null || parkingId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "error", "El 'parkingId' no puede ser nulo o vacío."
            ));
        }
        // Forzamos coherencia: el path manda.
        String authEmail = SecurityUtils.currentEmail();
        if (authEmail == null || authEmail.isBlank()) {
            return ResponseEntity.status(401).body(Map.of(
                    "code", 401,
                    "message", "No autenticado"
            ));
        }
        // Sobrescribir los valores
        request.setEmail(authEmail);
        request.setParkingId(parkingId);
        log.debug("Reservation request (sanitized) for user={}", authEmail);

        log.debug("Reservation request received: {}", request);
        ReservationResponse response = reservationService.makeReservation(authEmail, request);
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
    public ResponseEntity<?> cancelReservation(
            @PathVariable("id") String reservationId,
            @RequestBody CancelReservationRequest request) {

        log.debug("PATCH /reservations/{} - cancel request: {}", reservationId, request);

        String authEmail = SecurityUtils.currentEmail();
        if (authEmail == null || authEmail.isBlank()) {
            return ResponseEntity.status(401).body(Map.of(
                    "code", 401,
                    "message", "No autenticado"
            ));
        }
        request.setReservationId(reservationId);
        SimpleResponse response = reservationService.cancelReservation(authEmail, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reservations/{email}")
    public ResponseEntity<?> getReservationsByUser(
            @PathVariable("email") @Email String email
    ) {
        log.info("GET /reservations - email={}", email);
        if (email == null || email.isBlank() || email.isEmpty() || !email.contains("@")) {
            log.warn("Invalid email received: {}", email);
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error", "Parámetro inválido",
                            "message", "El parámetro 'email' debe ser un email valido.",
                            "status", 400
                    ));
        }

        var request = ReservationByUserRequestDto.builder()
                .email(email)
                .build();

        var response = reservationService.getReservationsByUser(email);

        log.info("GET /reservations - success - found={}",
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
