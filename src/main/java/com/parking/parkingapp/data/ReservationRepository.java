package com.parking.parkingapp.data;
import com.parking.parkingapp.data.model.Place;
import com.parking.parkingapp.data.model.Reservation;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ReservationRepository extends ElasticsearchRepository<Reservation, String> {
    List<Reservation> findAll();
    Reservation save(Reservation reserva);
    void deleteById(String reservationId);
}
