package com.parking.parkingapp.dto.Reservas.CancelarReserva;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelReservationRequest {
    private Integer floorNumber;
    private String reservationId;
    private String parkingId;
    private String spotId;
}