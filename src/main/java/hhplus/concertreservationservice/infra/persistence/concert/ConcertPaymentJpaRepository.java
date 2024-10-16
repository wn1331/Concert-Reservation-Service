package hhplus.concertreservationservice.infra.persistence.concert;

import hhplus.concertreservationservice.domain.concert.entity.ConcertPayment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertPaymentJpaRepository extends JpaRepository<ConcertPayment,Long> {


}
