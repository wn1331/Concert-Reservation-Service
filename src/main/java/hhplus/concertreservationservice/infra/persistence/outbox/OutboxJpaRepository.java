package hhplus.concertreservationservice.infra.persistence.outbox;

import hhplus.concertreservationservice.domain.outbox.entity.Outbox;
import hhplus.concertreservationservice.domain.outbox.entity.Outbox.OutboxStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxJpaRepository extends JpaRepository<Outbox,String> {

    Optional<Outbox> findByIdAndStatus(String id, OutboxStatus outboxStatus);

    List<Outbox> findAllByStatusIsAndModifiedAtBefore(OutboxStatus status, LocalDateTime validationType);
}
