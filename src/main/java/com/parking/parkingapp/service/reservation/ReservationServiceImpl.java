package com.parking.parkingapp.service.reservation;

import com.parking.parkingapp.data.DataAccessRepository;
import com.parking.parkingapp.data.ParkingRepository;
import com.parking.parkingapp.data.ReservationRepository;
import com.parking.parkingapp.data.model.Place;
import com.parking.parkingapp.data.model.Plaza;
import com.parking.parkingapp.data.model.Reservation;
import com.parking.parkingapp.dto.ReservarPlaza.ReservationRequest;
import com.parking.parkingapp.dto.ReservarPlaza.ReservationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl {

    private final ReservationRepository reservationRepository;
    private final DataAccessRepository parkingRepository;


    public ReservationResponse reserve(ReservationRequest request) {
        log.info("Intentando reservar plaza {} en el parking {}",
                request.getPlazaId(), request.getParkingId());

        try {
            List<Place> result = parkingRepository.findPlaceWithPlazasInPiso(
                    request.getParkingId(), request.getPisoId()
            );
            if (result.isEmpty()) {
                log.warn("No se encontró ningún place con parkingId {} y pisoId {}", request.getParkingId(), request.getPisoId());
                return new ReservationResponse(404, "No se encontró el parqueadero o piso especificado");
            }
            // 2. Buscar la plaza específica dentro del resultado
            Place place = result.get(0); // suponiendo que solo hay un lugar por combinación parkingId + pisoId
            Plaza plazaAReservar = place.getPlazas().stream()
                    .filter(p -> p.getId().equals(request.getPlazaId()))
                    .findFirst()
                    .orElse(null);

            if (plazaAReservar == null) {
                log.warn("La plaza {} no existe en el parking {} y piso {}", request.getPlazaId(), request.getParkingId(), request.getPisoId());
                return new ReservationResponse(404, "La plaza no existe");
            }

            if (Boolean.TRUE.equals(plazaAReservar.getOcupado())) {
                log.warn("La plaza {} ya está ocupada", request.getPlazaId());
                return new ReservationResponse(409, "La plaza ya está ocupada");
            }

            log.info("Reserva creada exitosamente: {}", plazaAReservar);

            return new ReservationResponse(200, "La reserva se ha realizado correctamente");

        } catch (Exception e) {
            log.error("Error al realizar la reserva", e);
            throw new RuntimeException("Error interno al realizar la reserva");
        }
    }
}
