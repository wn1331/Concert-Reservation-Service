package hhplus.concertreservationservice.domain.queue.repository;

import hhplus.concertreservationservice.domain.queue.entity.Queue;
import java.util.Optional;

public interface QueueRepository {

    Queue save(Queue queue);

    boolean existsByUserId(Long userId);

    Optional<Queue> findByUserId(Long userId);
}
