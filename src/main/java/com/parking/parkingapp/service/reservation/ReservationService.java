package com.parking.parkingapp.service.reservation;
import com.parking.parkingapp.dto.Reservas.ConsultaReservas.SearchReservationResponse;
import com.parking.parkingapp.dto.Reservas.ReservarPlaza.ReservationRequest;
import com.parking.parkingapp.dto.Reservas.ReservarPlaza.ReservationResponse;

public interface ReservationService {
    ReservationResponse reserve(ReservationRequest request);
    SearchReservationResponse getReservationAll();
}
