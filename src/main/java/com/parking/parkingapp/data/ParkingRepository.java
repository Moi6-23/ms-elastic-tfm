package com.parking.parkingapp.data;
import com.parking.parkingapp.data.model.Place;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ParkingRepository extends ElasticsearchRepository<Place, String> {

	List<Place> findAll();
}
