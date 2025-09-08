package com.parking.parkingapp.service.reservation;
import com.parking.parkingapp.data.DataAccessRepository;
import com.parking.parkingapp.data.model.Details;
import com.parking.parkingapp.data.model.Places;
import com.parking.parkingapp.data.model.Spot;
import com.parking.parkingapp.data.model.Reservation;
import com.parking.parkingapp.dto.Reservas.CancelarReserva.CancelReservationRequest;
import com.parking.parkingapp.dto.Reservas.ConsultaReservas.ReservationByUserRequestDto;
import com.parking.parkingapp.dto.Reservas.ConsultaReservas.SearchReservationByUserResponse;
import com.parking.parkingapp.dto.Reservas.ConsultaReservas.SearchReservationResponse;
import com.parking.parkingapp.dto.Reservas.ReservarPlaza.ReservationRequest;
import com.parking.parkingapp.dto.Reservas.ReservarPlaza.ReservationResponse;
import com.parking.parkingapp.dto.SimpleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final DataAccessRepository dataAccessRepository;

    @Override
    public ReservationResponse makeReservation(String authEmail, ReservationRequest request) {
        log.info("Attempting to reserve spot {} in parking {}", request.getSpotId(), request.getParkingId());

        try {
            final String email = request.getEmail() == null ? "" : authEmail.trim().toLowerCase();
            final String plate = request.getCarPlate() == null ? "" : request.getCarPlate().trim().toUpperCase();

            if (email.isBlank() || plate.isBlank()) {
                log.warn("Invalid payload: email or carPlate empty");
                return new ReservationResponse(400, "Email y placa son obligatorios");
            }

            // 1) Verificar si ya existe una reserva
            SearchReservationByUserResponse existing = dataAccessRepository.findReservationsByUser(email);
            if (hasAnyActiveReservation(existing)) {
                log.warn("User {} already has a reservation. Denying new reservation.", email);
                return new ReservationResponse(409, "El usuario ya tiene una reserva activa");
            }

            // 2) Verificar que el spot exista
            Optional<PlaceAndSpotResult> resultOpt = findPlaceAndSpot(
                    request.getParkingId(),
                    request.getFloorNumber(),
                    request.getSpotId()
            );

            if (resultOpt.isEmpty()) {
                return new ReservationResponse(404, "The spot does not exist in the specified parking/floor");
            }

            PlaceAndSpotResult result = resultOpt.get();
            if (Boolean.TRUE.equals(result.spot().getIsOccupied())) {
                log.warn("The spot {} is already occupied", request.getSpotId());
                return new ReservationResponse(409, "The spot is already occupied");
            }

            // 3) Crear reserva
            Reservation reservation = Reservation.builder()
                    .parkingId(request.getParkingId())
                    .spotId(request.getSpotId())
                    .floorNumber(request.getFloorNumber())
                    .carPlate(plate)
                    .email(email)
                    .createdAt(LocalDateTime.now())
                    .build();

            dataAccessRepository.saveOrUpdateReservation(reservation);
            log.info("Reservation created successfully: {}", reservation);

            // 4) Actualizar estado del spot
            result.spot().setIsOccupied(true);
            refreshAvailableSpots(result.place());
            dataAccessRepository.saveOrUpdatePlaces(result.place());
            log.info("Spot {} marked as occupied", request.getSpotId());

            return new ReservationResponse(200, "Reservation completed successfully");

        } catch (Exception e) {
            log.error("Error while making reservation", e);
            return new ReservationResponse(500, "Internal error while making reservation");
        }
    }

    @Override
    public SearchReservationResponse getAllReservations() {
        return dataAccessRepository.findAllReservations();
    }

    @Override
    public SearchReservationByUserResponse getReservationsByUser(String authEmail) {
        log.debug("Buscando reservas por usuario. email={}", authEmail);
        return dataAccessRepository.findReservationsByUser(authEmail);
    }

    @Override
    public SimpleResponse cancelReservation(String authEmail, CancelReservationRequest request) {
        Optional<Reservation> optional = dataAccessRepository.findByIdAndEmail(authEmail, request.getReservationId());

        if (optional.isEmpty()) {
            log.warn("Reservation {} not found", request.getReservationId());

            return new SimpleResponse(
                    404,
                    String.format("Reservation not found. id=%s, user=%s", request.getReservationId(), authEmail)
            );
        }

        dataAccessRepository.deleteReservation(request.getReservationId());
        log.info("Reservation {} successfully deleted", request.getReservationId());

        Optional<PlaceAndSpotResult> resultOpt = findPlaceAndSpot(request.getParkingId(), request.getFloorNumber(), request.getSpotId());

        if (resultOpt.isEmpty()) {
            return new SimpleResponse(404, "The spot does not exist in the specified parking/floor");
        }

        PlaceAndSpotResult result = resultOpt.get();
        result.spot().setIsOccupied(false);
        refreshAvailableSpots(result.place());
        dataAccessRepository.saveOrUpdatePlaces(result.place());
        log.info("Spot {} marked as unoccupied", request.getSpotId());

        return new SimpleResponse(200, "Reservation cancelled");
    }

    // Helper record to group Place and Spot
    private record PlaceAndSpotResult(Places place, Spot spot) {}

    // Reusable method to find Place and Spot
    private Optional<PlaceAndSpotResult> findPlaceAndSpot(String parkingId, int floorNumber, String spotId) {
        List<Places> result = dataAccessRepository.findPlaceWithPlazasInPiso(parkingId, floorNumber);

        if (result.isEmpty()) {
            log.warn("No place found with parkingId {} and floorId {}", parkingId, floorNumber);
            return Optional.empty();
        }

        Places place = result.get(0);
        log.debug("Loaded Place --------------------------------, order = {}", result.get(0).getOrder());

        Spot spot = place.getSpots().stream()
                .filter(p -> p.getId().equals(spotId))
                .findFirst()
                .orElse(null);

        if (spot == null) {
            log.warn("Spot {} does not exist in parking {} and floor {}", spotId, parkingId, floorNumber);
            return Optional.empty();
        }

        return Optional.of(new PlaceAndSpotResult(place, spot));
    }

    /**
     * Recalcula y actualiza el campo availableSpots
     * del primer Details de un Places, sumando todos
     * los spots que NO están ocupados.
     */
    private void refreshAvailableSpots(Places place) {
        int libres = (int) place.getSpots().stream()
                .filter(s -> !Boolean.TRUE.equals(s.getIsOccupied()))
                .count();

        // Asumimos que siempre hay al menos un Details
        Details detalle = place.getDetails().get(0);
        detalle.setAvailableSpots(libres);
    }

    private boolean hasAnyActiveReservation(SearchReservationByUserResponse resp) {
        return resp != null
                && resp.getReservations() != null
                && !resp.getReservations().isEmpty();
    }

}
