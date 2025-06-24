package com.parking.parkingapp.data;
import com.parking.parkingapp.data.model.Reservation;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends ElasticsearchRepository<Reservation, String> {
    List<Reservation> findAll();
    Reservation save(Reservation reserva);
    Optional<Reservation> deleteById(String reservationId);
    Optional<Reservation> findById(String id);

}
