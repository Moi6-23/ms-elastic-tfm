package com.parking.parkingapp.data;
import com.parking.parkingapp.data.model.Places;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Optional;

public interface ParkingRepository extends ElasticsearchRepository<Places, String> {

	List<Places> findAll();
	Optional<Places> findById(String id);
	Places save(Places parking);

}
