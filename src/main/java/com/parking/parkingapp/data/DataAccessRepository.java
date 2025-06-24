package com.parking.parkingapp.data;
import com.parking.parkingapp.controller.model.ParkingsQueryResponse;
import com.parking.parkingapp.controller.model.ParkingsWithoutResponse;
import com.parking.parkingapp.data.model.Place;
import com.parking.parkingapp.data.model.PlaceWithout;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

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
}