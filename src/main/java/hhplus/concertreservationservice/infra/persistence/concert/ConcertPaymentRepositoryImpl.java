package hhplus.concertreservationservice.infra.persistence.concert;

import hhplus.concertreservationservice.domain.concert.repository.ConcertPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConcertPaymentRepositoryImpl implements ConcertPaymentRepository {

    private final ConcertPaymentJpaRepository jpaRepository;

}
