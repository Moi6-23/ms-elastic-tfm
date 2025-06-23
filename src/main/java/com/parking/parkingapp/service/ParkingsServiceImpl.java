package com.parking.parkingapp.service;

import com.parking.parkingapp.controller.model.ParkingsQueryResponse;
import com.parking.parkingapp.data.DataAccessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ParkingsServiceImpl implements ParkingsService {

	private final DataAccessRepository repository;

	@Override
	public ParkingsQueryResponse getParkings() {
		return repository.findAllPlaces();
	}
}
