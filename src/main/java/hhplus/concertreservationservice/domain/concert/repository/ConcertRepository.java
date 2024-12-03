package hhplus.concertreservationservice.domain.concert.repository;

import hhplus.concertreservationservice.domain.concert.entity.Concert;
import java.util.List;
import java.util.Optional;

public interface ConcertRepository {

    List<Concert> findAll();

    Concert save(Concert concert);

    Optional<Concert> findById(Long id);
}
