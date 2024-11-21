package hhplus.concertreservationservice.domain.outbox.repository;

import hhplus.concertreservationservice.domain.outbox.entity.Outbox;
import hhplus.concertreservationservice.domain.outbox.entity.Outbox.OutboxStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OutboxRepository {

    Outbox save(Outbox entity);

    Optional<Outbox> findByIdAndStatus(String id, OutboxStatus status);

    List<Outbox> findFailedOutbox(LocalDateTime validationType);
}
