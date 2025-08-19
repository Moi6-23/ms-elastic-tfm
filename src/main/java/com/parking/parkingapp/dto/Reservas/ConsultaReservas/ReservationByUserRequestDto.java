package com.parking.parkingapp.dto.Reservas.ConsultaReservas;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationByUserRequestDto {
    @NotBlank
    @Email
    private String email;
}