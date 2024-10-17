package hhplus.concertreservationservice.infra.persistence.concert;

import hhplus.concertreservationservice.domain.concert.entity.ConcertPayment;
import hhplus.concertreservationservice.domain.concert.repository.ConcertPaymentRepository;
import java.util.Optional;
import java.util.OptionalInt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConcertPaymentRepositoryImpl implements ConcertPaymentRepository {

    private final ConcertPaymentJpaRepository jpaRepository;


    @Override
    public ConcertPayment save(ConcertPayment concertPayment) {
        return jpaRepository.save(concertPayment);
    }
}
