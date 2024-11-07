package hhplus.concertreservationservice.domain.concert.repository;

import hhplus.concertreservationservice.domain.concert.entity.ConcertSeat;
import hhplus.concertreservationservice.domain.concert.entity.SeatStatusType;
import java.util.List;
import java.util.Optional;

public interface ConcertSeatRepository {

    void save(ConcertSeat seat);

    void saveAll(List<ConcertSeat> seats);

    List<ConcertSeat> findByConcertScheduleIdAndStatus(Long concertScheduleId);

    Optional<ConcertSeat> findById(Long seatId);

    Optional<ConcertSeat> findByIdAndStatus(Long concertSeatId, SeatStatusType status);
}
