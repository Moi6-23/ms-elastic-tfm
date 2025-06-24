package com.parking.parkingapp.dto.Reservas.ReservarPlaza;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResponse {
    private int code;
    private String message;
}
