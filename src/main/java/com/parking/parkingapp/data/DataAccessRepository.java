package com.parking.parkingapp.data;
import com.parking.parkingapp.data.model.Spot;
import com.parking.parkingapp.data.model.Reservation;
import com.parking.parkingapp.dto.ParkingsDto.Parkings.ParkingsQueryResponse;
import com.parking.parkingapp.dto.ParkingsDto.Parkings.ParkingsWithoutResponse;
import com.parking.parkingapp.data.model.Places;
import com.parking.parkingapp.data.model.PlaceWithout;
import com.parking.parkingapp.dto.Reservas.ConsultaReservas.SearchReservationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.ResourceNotFoundException;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DataAccessRepository {

    private final ParkingRepository parkingRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ReservationRepository reservationRepository;

    public ParkingsQueryResponse findAllPlaces() {
        List<Places> places = parkingRepository.findAll();
        return new ParkingsQueryResponse(places);
    }

    public ParkingsWithoutResponse findAllPlacesWithoutPlaces() {
        log.debug("Ejecutando búsqueda de places sin plazas");
        Query query = new NativeSearchQueryBuilder()
                .withSourceFilter(new FetchSourceFilter(
                        new String[]{"id", "name", "city", "details"},  // Campos que deseas incluir
                        null))
                .build();

        SearchHits<PlaceWithout> searchHits = elasticsearchOperations.search(
                query,
                PlaceWithout.class
        );

        List<PlaceWithout> filteredPlaces = searchHits.stream()
                .map(hit -> hit.getContent())
                .toList();
        return new ParkingsWithoutResponse(filteredPlaces);
    }

    public List<Places> findPlaceWithPlazasInPiso(String parkingId, int piso) {
        Optional<Places> result = parkingRepository.findById(parkingId);
        if (result.isEmpty()) {
            throw new ResourceNotFoundException("No se encontró un parqueadero con el ID: " + parkingId);
        }

        Places place = result.get();

        List<Spot> plazasEnPiso = place.getSpots().stream()
                .filter(plaza -> plaza.getFloorNumber() != null && plaza.getFloorNumber() == piso)
                .collect(Collectors.toList());

        if (plazasEnPiso.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron plazas para el piso: " + piso + " en el parqueadero con ID: " + parkingId);
        }

        place.setSpots(plazasEnPiso);

        return List.of(place); // Mantener la interfaz de respuesta como lista
    }

    public Reservation saveOrUpdateReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public Places saveOrUpdatePlaces(Places place) {
        return parkingRepository.save(place);
    }

    public SearchReservationResponse findAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        return new SearchReservationResponse(reservations);
    }

    public Optional<Reservation> deleteReservation(String reservationId){
        return reservationRepository.deleteById(reservationId);
    }

    public Optional<Reservation> findByIdReservation(String reservationId) {
        return reservationRepository.findById(reservationId);
    }

}