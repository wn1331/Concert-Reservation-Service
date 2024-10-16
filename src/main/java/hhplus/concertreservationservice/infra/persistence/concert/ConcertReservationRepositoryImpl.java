package hhplus.concertreservationservice.infra.persistence.concert;

import hhplus.concertreservationservice.domain.concert.entity.ConcertReservation;
import hhplus.concertreservationservice.domain.concert.entity.ReservationStatusType;
import hhplus.concertreservationservice.domain.concert.repository.ConcertReservationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConcertReservationRepositoryImpl implements ConcertReservationRepository {

    private final ConcertReservationJpaRepository jpaRepository;

    @Override
    public ConcertReservation save(ConcertReservation concertReservation) {
        return jpaRepository.save(concertReservation);
    }

    @Override
    public Optional<ConcertReservation> findById(Long reservationId
        ) {
        return jpaRepository.findById(reservationId);
    }

    @Override
    public List<ConcertReservation> findExpiredReservations(
        ReservationStatusType reservationStatusType, LocalDateTime minusFiveMinAtNow) {
        return jpaRepository.findExpiredReservations(reservationStatusType,minusFiveMinAtNow);
    }

}
