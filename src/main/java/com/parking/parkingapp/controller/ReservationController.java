package com.parking.parkingapp.controller;
import com.parking.parkingapp.dto.Reservas.CancelarReserva.CancelReservationRequest;
import com.parking.parkingapp.dto.Reservas.ConsultaReservas.SearchReservationResponse;
import com.parking.parkingapp.dto.Reservas.ReservarPlaza.ReservationRequest;
import com.parking.parkingapp.dto.Reservas.ReservarPlaza.ReservationResponse;
import com.parking.parkingapp.dto.SimpleResponse;
import com.parking.parkingapp.service.reservation.ReservationServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
}
