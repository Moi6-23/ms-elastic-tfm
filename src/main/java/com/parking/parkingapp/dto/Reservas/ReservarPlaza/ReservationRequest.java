package com.parking.parkingapp.dto.Reservas.ReservarPlaza;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationRequest {
    private String parkingId;
    private String spotId;
    private int floorNumber;
    private String carPlate;
    private String email;
}
