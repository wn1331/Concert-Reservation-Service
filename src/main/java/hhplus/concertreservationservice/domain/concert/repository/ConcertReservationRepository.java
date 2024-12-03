package hhplus.concertreservationservice.domain.concert.repository;

import hhplus.concertreservationservice.domain.concert.entity.ConcertReservation;
import hhplus.concertreservationservice.domain.concert.entity.ReservationStatusType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConcertReservationRepository {

    ConcertReservation save(ConcertReservation concertReservation);

    Optional<ConcertReservation> findById(Long reservationId);

    List<ConcertReservation> findExpiredReservations(ReservationStatusType reservationStatusType, LocalDateTime localDateTime);
}
