package com.parking.parkingapp.service;
import lombok.extern.slf4j.Slf4j;
import com.parking.parkingapp.controller.model.ParkingsQueryResponse;
import com.parking.parkingapp.controller.model.ParkingsWithoutResponse;
import com.parking.parkingapp.data.DataAccessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingsServiceImpl implements ParkingsService {

	private final DataAccessRepository repository;

	@Override
	public ParkingsQueryResponse getParkings() {
		return repository.findAllPlaces();
	}

	@Override
	public ParkingsWithoutResponse getParkingswithout() {
		log.debug("Buscando places without places");
		return repository.findAllPlacesWithoutPlaces();
	}

}
