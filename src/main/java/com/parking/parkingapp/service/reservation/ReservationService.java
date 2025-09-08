package com.parking.parkingapp.service.reservation;
import com.parking.parkingapp.dto.Reservas.CancelarReserva.CancelReservationRequest;
import com.parking.parkingapp.dto.Reservas.ConsultaReservas.ReservationByUserRequestDto;
import com.parking.parkingapp.dto.Reservas.ConsultaReservas.SearchReservationByUserResponse;
import com.parking.parkingapp.dto.Reservas.ConsultaReservas.SearchReservationResponse;
import com.parking.parkingapp.dto.Reservas.ReservarPlaza.ReservationRequest;
import com.parking.parkingapp.dto.Reservas.ReservarPlaza.ReservationResponse;
import com.parking.parkingapp.dto.SimpleResponse;

public interface ReservationService {
    ReservationResponse makeReservation(String authEmail, ReservationRequest request);
    SearchReservationResponse getAllReservations();
    SimpleResponse cancelReservation(String authEmail,CancelReservationRequest request);
    SearchReservationByUserResponse getReservationsByUser(String authEmail);

}
