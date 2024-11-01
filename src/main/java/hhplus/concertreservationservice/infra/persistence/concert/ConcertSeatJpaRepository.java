package hhplus.concertreservationservice.infra.persistence.concert;

import hhplus.concertreservationservice.domain.concert.entity.ConcertSeat;
import hhplus.concertreservationservice.domain.concert.entity.SeatStatusType;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface ConcertSeatJpaRepository extends JpaRepository<ConcertSeat,Long> {

    List<ConcertSeat> findByConcertScheduleIdAndStatus(Long concertScheduleId, SeatStatusType seatStatusType);

    Optional<ConcertSeat> findByIdAndStatus(Long concertSeatId, SeatStatusType status);

    Optional<ConcertSeat> findById(Long concertSeatId);
}
