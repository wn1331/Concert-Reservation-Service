package hhplus.concertreservationservice.infra.persistence.concert;

import hhplus.concertreservationservice.domain.concert.entity.ConcertPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertPaymentJpaRepository extends JpaRepository<ConcertPayment,Long> {

}
