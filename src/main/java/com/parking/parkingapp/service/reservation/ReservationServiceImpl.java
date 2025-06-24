package com.parking.parkingapp.service.reservation;

import com.parking.parkingapp.data.DataAccessRepository;
import com.parking.parkingapp.data.model.Place;
import com.parking.parkingapp.data.model.Plaza;
import com.parking.parkingapp.data.model.Reservation;
import com.parking.parkingapp.dto.ParkingsDto.Parkings.ParkingsQueryResponse;
import com.parking.parkingapp.dto.Reservas.ConsultaReservas.SearchReservationResponse;
import com.parking.parkingapp.dto.Reservas.ReservarPlaza.ReservationRequest;
import com.parking.parkingapp.dto.Reservas.ReservarPlaza.ReservationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService{

    private final DataAccessRepository dataAccessRepository;

    @Override
    public ReservationResponse reserve(ReservationRequest request) {
        log.info("Intentando reservar plaza {} en el parking {}",
                request.getPlazaId(), request.getParkingId());

        try {
            List<Place> result = dataAccessRepository.findPlaceWithPlazasInPiso(
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
            // 3. Crear la reserva
            Reservation reservation = Reservation.builder()
                    .parkingId(request.getParkingId())
                    .plazaId(request.getPlazaId())
                    .pisoId(request.getPisoId())
                    .placa(request.getPlaca())
                    .userEmail(request
                            .getUserEmail()) // en pruebas, lo incluyes en el payload
                    .createdAt(LocalDateTime.now())
                    .build();

            dataAccessRepository.saveOrUpdateReservation(reservation);
            log.info("Reserva creada exitosamente: {}", reservation);

            // 4. Marcar plaza como ocupada y guardar el documento actualizado
            plazaAReservar.setOcupado(true);
            dataAccessRepository.saveOrUpdatePlaces(place);
            log.info("Plaza {} marcada como ocupada en el índice places", request.getPlazaId());

            //return new ReservationResponse(200, "La reserva se ha realizado correctamente");
            //log.info("Reserva creada exitosamente: {}", plazaAReservar);

            return new ReservationResponse(200, "La reserva se ha realizado correctamente");

        } catch (Exception e) {
            log.error("Error al realizar la reserva", e);
            throw new RuntimeException("Error interno al realizar la reserva");
        }
    }

    @Override
    public SearchReservationResponse getReservationAll() {
        return dataAccessRepository.findAllReservations();
    }
}
