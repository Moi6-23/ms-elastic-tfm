package com.parking.parkingapp.service.reservation;
import com.parking.parkingapp.dto.Reservas.CancelarReserva.CancelReservationRequest;
import com.parking.parkingapp.dto.Reservas.ConsultaReservas.SearchReservationResponse;
import com.parking.parkingapp.dto.Reservas.ReservarPlaza.ReservationRequest;
import com.parking.parkingapp.dto.Reservas.ReservarPlaza.ReservationResponse;
import com.parking.parkingapp.dto.SimpleResponse;

public interface ReservationService {
    ReservationResponse makeReservation(ReservationRequest request);
    SearchReservationResponse getAllReservations();
    SimpleResponse cancelReservation(CancelReservationRequest request);
}
