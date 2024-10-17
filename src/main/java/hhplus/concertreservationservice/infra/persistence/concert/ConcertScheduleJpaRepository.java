package hhplus.concertreservationservice.infra.persistence.concert;

import hhplus.concertreservationservice.domain.concert.entity.ConcertSchedule;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertScheduleJpaRepository extends JpaRepository<ConcertSchedule,Long> {

    List<ConcertSchedule> findByConcertIdAndConcertDateAfter(Long concertId, LocalDate date);
}
