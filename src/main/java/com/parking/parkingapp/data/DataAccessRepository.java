package com.parking.parkingapp.data;
import com.parking.parkingapp.controller.model.ParkingsQueryResponse;
import com.parking.parkingapp.data.model.Place;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DataAccessRepository {

    private final ParkingRepository parkingRepository;

    public ParkingsQueryResponse findAllPlaces() {
        List<Place> places = parkingRepository.findAll();
        return new ParkingsQueryResponse(places);
    }
}