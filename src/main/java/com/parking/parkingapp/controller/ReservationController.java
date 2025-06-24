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
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationServiceImpl reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> reservePlace(@RequestBody ReservationRequest request) {
        log.debug("Reservation request received: {}", request);
        ReservationResponse response = reservationService.makeReservation(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<SearchReservationResponse> getReservation() {
        log.info("GET /reservations - start");
        SearchReservationResponse reservations = reservationService.getAllReservations();
        log.info("GET /reservations - success");
        return ResponseEntity.ok(reservations);
    }

    @PostMapping("/cancel")
    public ResponseEntity<SimpleResponse> cancelReservation(@RequestBody CancelReservationRequest request) {
        log.debug("Cancel reservation request received: {}", request);
        SimpleResponse response = reservationService.cancelReservation(request);
        return ResponseEntity.ok(response);
    }
}
