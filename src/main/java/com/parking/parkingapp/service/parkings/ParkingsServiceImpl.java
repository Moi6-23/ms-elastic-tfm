package com.parking.parkingapp.service.parkings;

import com.parking.parkingapp.data.model.Places;
import com.parking.parkingapp.data.model.Spot;
import com.parking.parkingapp.dto.ParkingsDto.ParkingOnly.ParkingOnlyRequestDto;
import com.parking.parkingapp.dto.ParkingsDto.ParkingOnly.ParkingOnlyResponseDto;
import com.parking.parkingapp.dto.ParkingsDto.UpdateParking.ParkingUpdateRequestDto;
import com.parking.parkingapp.dto.SimpleResponse;
import com.parking.parkingapp.dto.ParkingsDto.Parkings.ParkingsQueryResponse;
import com.parking.parkingapp.dto.ParkingsDto.Parkings.ParkingsWithoutResponse;
import com.parking.parkingapp.data.DataAccessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
	public ParkingsWithoutResponse getParkingsWithout() {
		log.debug("Searching for places without spots");
		return repository.findAllPlacesWithoutPlaces();
	}

	@Override
	public ParkingOnlyResponseDto findPlaceWithSpotsOnFloor(ParkingOnlyRequestDto request) {
		log.info("Starting spot query");
		log.debug("Received request - parkingId: {}, floor: {}", request.getParkingId(), request.getFloorNumber());
		List<Places> places = repository.findPlaceWithPlazasInPiso(request.getParkingId(), request.getFloorNumber());
		log.info("{} place(s) with spots found", places.size());
		log.debug("Result content: {}", places);
		ParkingOnlyResponseDto response = ParkingOnlyResponseDto.builder()
				.code(200)
				.data(places)
				.build();
		log.info("Response built successfully");
		return response;
	}

	@Override
	public SimpleResponse updateSpotStatus(ParkingUpdateRequestDto request) {
		log.info("Attempting to update spot {} in parking {}", request.getSpotId(), request.getParkingId());

		try {
			List<Places> result = repository.findPlaceWithPlazasInPiso(
					request.getParkingId(), request.getFloorNumber()
			);
			if (result.isEmpty()) {
				log.warn("No place found with parkingId {} and floorId {}", request.getParkingId(), request.getSpotId());
				return new SimpleResponse(404, "Parking or floor not found");
			}

			// Search for the specific spot within the result
			Places place = result.get(0); // assuming only one place per parkingId + floorNumber combination
			Spot spotToUpdate = place.getSpots().stream()
					.filter(p -> p.getId().equals(request.getSpotId()))
					.findFirst()
					.orElse(null);

			if (spotToUpdate == null) {
				log.warn("Spot {} does not exist in parking {} and floor {}", request.getSpotId(), request.getParkingId(), request.getFloorNumber());
				return new SimpleResponse(404, "Spot not found");
			}

			if (spotToUpdate.getIsOccupied() == request.getStatus()) {
				log.warn("Spot {} is already in the requested status", request.getSpotId());
				return new SimpleResponse(409, "Spot is already in the requested status");
			}

			spotToUpdate.setIsOccupied(request.getStatus());
			repository.saveOrUpdatePlaces(place);
			log.info("Spot {} marked as occupied in the places index", request.getSpotId());

			return new SimpleResponse(200, "Spot status updated");

		} catch (Exception e) {
			log.error("Error while updating spot status", e);
			throw new RuntimeException("Internal error while updating spot status");
		}
	}
}
