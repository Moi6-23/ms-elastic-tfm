package com.parking.parkingapp.dto.Reservas.ConsultaReservas;

import com.parking.parkingapp.data.model.Reservation;
import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class SearchReservationByUserResponse {
    private List<Reservation> reservations;
}