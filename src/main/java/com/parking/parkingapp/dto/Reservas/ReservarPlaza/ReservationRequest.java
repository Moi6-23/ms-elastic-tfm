package com.parking.parkingapp.dto.Reservas.ReservarPlaza;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationRequest {
    private String parkingId;
    private String plazaId;
    private int pisoId;
    private String placa;
    private String userEmail;
}
