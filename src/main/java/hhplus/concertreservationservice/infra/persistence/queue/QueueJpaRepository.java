package hhplus.concertreservationservice.infra.persistence.queue;

import hhplus.concertreservationservice.domain.queue.entity.Queue;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueueJpaRepository extends JpaRepository<Queue,Long> {

    boolean existsByUserId(Long userId);

    Optional<Queue> findByUserId(Long userId);
}
