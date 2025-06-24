package com.parking.parkingapp.data;
import com.parking.parkingapp.data.model.Reservation;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ReservationRepository extends ElasticsearchRepository<Reservation, String> {}
