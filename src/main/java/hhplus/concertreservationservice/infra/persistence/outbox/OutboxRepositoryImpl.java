package hhplus.concertreservationservice.infra.persistence.outbox;

import hhplus.concertreservationservice.domain.outbox.entity.Outbox;
import hhplus.concertreservationservice.domain.outbox.entity.Outbox.OutboxStatus;
import hhplus.concertreservationservice.domain.outbox.repository.OutboxRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

    private final OutboxJpaRepository jpaRepository;


    @Override
    public Outbox save(Outbox entity) {
        return jpaRepository.save(entity);
    }

    @Override
    public Optional<Outbox> findByIdAndStatus(String id) {
        return jpaRepository.findByIdAndStatus(id, OutboxStatus.INIT);
    }

    @Override
    public List<Outbox> findFailedOutbox(LocalDateTime validationType) {
        return jpaRepository.findAllByStatusIsAndModifiedAtBefore(OutboxStatus.INIT, validationType);
    }
}
