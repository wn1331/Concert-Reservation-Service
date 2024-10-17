package hhplus.concertreservationservice.infra.persistence.queue;

import hhplus.concertreservationservice.domain.queue.entity.Queue;
import hhplus.concertreservationservice.domain.queue.entity.QueueStatusType;
import hhplus.concertreservationservice.domain.queue.repository.QueueRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
    public Optional<Queue> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId);
    }

    @Override
    public Optional<Queue> findByQueueToken(String queueToken) {
        return jpaRepository.findByQueueToken(queueToken);
    }

    @Override
    public void delete(Queue queue) {
        jpaRepository.delete(queue);
    }

    @Override
    public long countWaitingUsersBefore(Long id) {
        return jpaRepository.countWaitingUsersBefore(id);
    }

    @Override
    public List<Queue> findByStatusOrderByIdAsc(QueueStatusType queueStatusType,
        Pageable pageable) {
        return jpaRepository.findByStatusOrderByIdAsc(queueStatusType, pageable);
    }

    @Override
    public List<Queue> findExpiredQueues(QueueStatusType queueStatusType, LocalDateTime minusFiveMinAtNow) {
        return jpaRepository.findExpiredQueues(queueStatusType,minusFiveMinAtNow);
    }

    @Override
    public void deleteAllInBatch(List<Queue> expiredQueues) {
        jpaRepository.deleteAllInBatch(expiredQueues);
    }


}
