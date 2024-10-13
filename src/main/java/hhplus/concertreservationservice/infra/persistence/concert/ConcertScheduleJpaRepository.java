package hhplus.concertreservationservice.infra.persistence.concert;

import hhplus.concertreservationservice.domain.concert.entity.ConcertSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertScheduleJpaRepository extends JpaRepository<ConcertSchedule,Long> {

}
