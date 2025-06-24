package com.parking.parkingapp.dto.Reservas.ConsultaReservas;
import com.parking.parkingapp.data.model.Reservation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchReservationResponse {
    private List<Reservation> reservations;

}
