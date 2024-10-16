package hhplus.concertreservationservice.domain.concert.repository;

import hhplus.concertreservationservice.domain.concert.entity.ConcertSeat;
import hhplus.concertreservationservice.domain.concert.entity.SeatStatusType;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;

public interface ConcertSeatRepository {

    void save(ConcertSeat seat);

    void saveAll(List<ConcertSeat> seats);

    List<ConcertSeat> findByConcertScheduleIdAndStatus(Long concertScheduleId);

    @Lock(LockModeType.OPTIMISTIC)
    Optional<ConcertSeat> findById(Long seatId);

    Optional<ConcertSeat> findByIdAndStatus(Long concertSeatId, SeatStatusType status);
}
