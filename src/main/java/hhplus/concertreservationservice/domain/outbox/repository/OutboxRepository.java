package hhplus.concertreservationservice.domain.outbox.repository;

import hhplus.concertreservationservice.domain.outbox.entity.Outbox;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OutboxRepository {

    Outbox save(Outbox entity);

    Optional<Outbox> findByIdAndStatus(String id);

    List<Outbox> findFailedOutbox(LocalDateTime validationType);
}
