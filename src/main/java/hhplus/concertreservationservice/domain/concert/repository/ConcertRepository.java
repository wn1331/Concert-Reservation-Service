package hhplus.concertreservationservice.domain.concert.repository;

import hhplus.concertreservationservice.domain.concert.entity.Concert;

public interface ConcertRepository {

    void save(Concert concert);
}
