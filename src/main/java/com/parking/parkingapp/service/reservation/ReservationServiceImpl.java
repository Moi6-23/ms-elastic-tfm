package com.parking.parkingapp.service.reservation;
import com.parking.parkingapp.data.DataAccessRepository;
import com.parking.parkingapp.data.model.Places;
import com.parking.parkingapp.data.model.Spot;
import com.parking.parkingapp.data.model.Reservation;
import com.parking.parkingapp.dto.Reservas.CancelarReserva.CancelReservationRequest;
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
    public ReservationResponse makeReservation(ReservationRequest request) {
        log.info("Attempting to reserve spot {} in parking {}", request.getSpotId(), request.getParkingId());

        try {
            Optional<PlaceAndSpotResult> resultOpt = findPlaceAndSpot(request.getParkingId(), request.getFloorNumber(), request.getSpotId());

            if (resultOpt.isEmpty()) {
                return new ReservationResponse(404, "The spot does not exist in the specified parking/floor");
            }

            PlaceAndSpotResult result = resultOpt.get();
            if (Boolean.TRUE.equals(result.spot().getIsOccupied())) {
                log.warn("The spot {} is already occupied", request.getSpotId());
                return new ReservationResponse(409, "The spot is already occupied");
            }

            Reservation reservation = Reservation.builder()
                    .parkingId(request.getParkingId())
                    .spotId(request.getSpotId())
                    .floorNumber(request.getFloorNumber())
                    .carPlate(request.getCarPlate())
                    .email(request.getEmail())
                    .createdAt(LocalDateTime.now())
                    .build();

            dataAccessRepository.saveOrUpdateReservation(reservation);
            log.info("Reservation created successfully: {}", reservation);

            result.spot().setIsOccupied(true);
            dataAccessRepository.saveOrUpdatePlaces(result.place());
            log.info("Spot {} marked as occupied", request.getSpotId());

            return new ReservationResponse(200, "Reservation completed successfully");

        } catch (Exception e) {
            log.error("Error while making reservation", e);
            throw new RuntimeException("Internal error while making reservation");
        }
    }

    @Override
    public SearchReservationResponse getAllReservations() {
        return dataAccessRepository.findAllReservations();
    }

    @Override
    public SimpleResponse cancelReservation(CancelReservationRequest request) {
        Optional<Reservation> optional = dataAccessRepository.findByIdReservation(request.getReservationId());

        if (optional.isEmpty()) {
            log.warn("Reservation {} not found", request.getReservationId());
            return new SimpleResponse(404, "Reservation not found");
        }

        dataAccessRepository.deleteReservation(request.getReservationId());
        log.info("Reservation {} successfully deleted", request.getReservationId());

        Optional<PlaceAndSpotResult> resultOpt = findPlaceAndSpot(request.getParkingId(), request.getFloorNumber(), request.getSpotId());

        if (resultOpt.isEmpty()) {
            return new SimpleResponse(404, "The spot does not exist in the specified parking/floor");
        }

        PlaceAndSpotResult result = resultOpt.get();
        result.spot().setIsOccupied(false);
        dataAccessRepository.saveOrUpdatePlaces(result.place());
        log.info("Spot {} marked as unoccupied", request.getSpotId());

        return new SimpleResponse(200, "Reservation cancelled");
    }

    // Helper record to group Place and Spot
    private record PlaceAndSpotResult(Places place, Spot spot) {}

    // Reusable method to find Place and Spot
    private Optional<PlaceAndSpotResult> findPlaceAndSpot(String parkingId, int floorId, String spotId) {
        List<Places> result = dataAccessRepository.findPlaceWithPlazasInPiso(parkingId, floorId);

        if (result.isEmpty()) {
            log.warn("No place found with parkingId {} and floorId {}", parkingId, floorId);
            return Optional.empty();
        }

        Places place = result.get(0);
        Spot spot = place.getSpots().stream()
                .filter(p -> p.getId().equals(spotId))
                .findFirst()
                .orElse(null);

        if (spot == null) {
            log.warn("Spot {} does not exist in parking {} and floor {}", spotId, parkingId, floorId);
            return Optional.empty();
        }

        return Optional.of(new PlaceAndSpotResult(place, spot));
    }
}
