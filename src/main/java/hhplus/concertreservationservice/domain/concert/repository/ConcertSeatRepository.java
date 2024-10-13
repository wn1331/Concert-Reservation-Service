package hhplus.concertreservationservice.domain.concert.repository;

import hhplus.concertreservationservice.domain.concert.entity.ConcertSeat;
import java.util.List;

public interface ConcertSeatRepository {

    void save(ConcertSeat seat);

    void saveAll(List<ConcertSeat> seats);
}
