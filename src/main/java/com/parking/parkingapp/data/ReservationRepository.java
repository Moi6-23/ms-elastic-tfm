package com.parking.parkingapp.data;
import com.parking.parkingapp.data.model.Reservation;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends ElasticsearchRepository<Reservation, String> {
    List<Reservation> findAll();
    Reservation save(Reservation reserva);
    Optional<Reservation> deleteById(String reservationId);
    Optional<Reservation> findById(String id);
    // Exact match sobre el subcampo keyword; el sort se pasa por parámetro
    @Query("""
    {
      "bool": {
        "filter": [
          { "term": { "email.keyword": "?0" } }
        ]
      }
    }
    """)
    List<Reservation> findByEmailExact(String email, Sort sort);

    @Query("""
      {
        "bool": {
          "filter": [
            { "ids": { "values": ["?0"] } },
            { "term": { "email.keyword": "?1" } }
          ]
        }
      }
      """)
    Optional<Reservation> findByIdAndEmail(String id, String email);

}
