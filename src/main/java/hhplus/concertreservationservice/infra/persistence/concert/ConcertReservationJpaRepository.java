package hhplus.concertreservationservice.infra.persistence.concert;

import hhplus.concertreservationservice.domain.concert.entity.ConcertReservation;
import hhplus.concertreservationservice.domain.concert.entity.ReservationStatusType;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ConcertReservationJpaRepository extends JpaRepository<ConcertReservation,Long> {

    @Query("SELECT c FROM ConcertReservation c WHERE c.status = :status AND c.createdAt < :minusFiveMinAtNow")
    List<ConcertReservation> findExpiredReservations(ReservationStatusType status, LocalDateTime minusFiveMinAtNow);
}
