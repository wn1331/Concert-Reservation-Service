package hhplus.concertreservationservice.infra.persistence.queue;

import hhplus.concertreservationservice.domain.queue.entity.Queue;
import hhplus.concertreservationservice.domain.queue.repository.QueueRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueueRepositoryImpl implements QueueRepository {

    private final QueueJpaRepository jpaRepository;

    @Override
    public Queue save(Queue queue) {
        return jpaRepository.save(queue);
    }

    @Override
    public boolean existsByUserId(Long userId) {
        return jpaRepository.existsByUserId(userId);
    }

    @Override
    public Optional<Queue> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId);
    }
}
