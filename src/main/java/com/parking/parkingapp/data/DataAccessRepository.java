package com.parking.parkingapp.data;
import com.parking.parkingapp.data.model.Plaza;
import com.parking.parkingapp.dto.Parkings.ParkingsQueryResponse;
import com.parking.parkingapp.dto.Parkings.ParkingsWithoutResponse;
import com.parking.parkingapp.data.model.Place;
import com.parking.parkingapp.data.model.PlaceWithout;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.ResourceNotFoundException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
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

    public ParkingsQueryResponse findAllPlaces() {
        List<Place> places = parkingRepository.findAll();
        return new ParkingsQueryResponse(places);
    }

    public ParkingsWithoutResponse findAllPlacesWithoutPlaces() {
        log.debug("Ejecutando búsqueda de places sin plazas");
        Query query = new NativeSearchQueryBuilder()
                .withSourceFilter(new FetchSourceFilter(
                        new String[]{"id", "parking", "city", "detail"},  // Campos que deseas incluir
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

    public List<Place> findPlaceWithPlazasInPiso(String parkingId, int piso) {
        Optional<Place> result = parkingRepository.findById(parkingId);
        if (result.isEmpty()) {
            throw new ResourceNotFoundException("No se encontró un parqueadero con el ID: " + parkingId);
        }

        Place place = result.get();

        List<Plaza> plazasEnPiso = place.getPlazas().stream()
                .filter(plaza -> plaza.getPiso() != null && plaza.getPiso() == piso)
                .collect(Collectors.toList());

        if (plazasEnPiso.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron plazas para el piso: " + piso + " en el parqueadero con ID: " + parkingId);
        }

        place.setPlazas(plazasEnPiso);

        return List.of(place); // Mantener la interfaz de respuesta como lista
    }
}