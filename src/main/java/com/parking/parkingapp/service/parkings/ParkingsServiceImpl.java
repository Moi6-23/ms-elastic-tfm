package com.parking.parkingapp.service.parkings;
import com.parking.parkingapp.data.model.Place;
import com.parking.parkingapp.dto.ParkingOnly.ParkingOnlyRequestDto;
import com.parking.parkingapp.dto.ParkingOnly.ParkingOnlyResponseDto;
import lombok.extern.slf4j.Slf4j;
import com.parking.parkingapp.dto.Parkings.ParkingsQueryResponse;
import com.parking.parkingapp.dto.Parkings.ParkingsWithoutResponse;
import com.parking.parkingapp.data.DataAccessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

	@Override
	public ParkingOnlyResponseDto findPlaceWithPlazasInPiso(ParkingOnlyRequestDto request) {
		log.info("Iniciando consulta de plazas");
		log.debug("Request recibido - parkingId: {}, piso: {}", request.getParkingId(), request.getPiso());
		List<Place> places = repository.findPlaceWithPlazasInPiso(request.getParkingId(), request.getPiso());
		log.info("Se encontraron {} lugar(es) con plazas", places.size());
		log.debug("Contenido del resultado: {}", places);
		ParkingOnlyResponseDto response = ParkingOnlyResponseDto.builder()
				.code(200)
				.data(places)
				.build();
		log.info("Respuesta construida correctamente");
		return response;
	}

}
