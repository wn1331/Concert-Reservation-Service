package hhplus.concertreservationservice.infra.persistence.queue;

import hhplus.concertreservationservice.domain.queue.entity.Queue;
import hhplus.concertreservationservice.domain.queue.entity.QueueStatusType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QueueJpaRepository extends JpaRepository<Queue,Long> {

    Optional<Queue> findByUserId(Long userId);

    Optional<Queue> findByQueueToken(String token);

    @Query("SELECT COUNT(q) FROM Queue q WHERE q.id < :id AND q.status = 'WAITING'")
    long countWaitingUsersBefore(Long id);



    List<Queue> findByStatusOrderByIdAsc(QueueStatusType queueStatusType, Pageable pageable);

    // status가 PASS이면서 modified_at이 5분 이상 지난 항목을 조회
    @Query("SELECT q FROM Queue q WHERE q.status = :status AND q.modifiedAt < :minusFiveMinAtNow")
    List<Queue> findExpiredQueues(@Param("status") QueueStatusType status, @Param("minusFiveMinAtNow") LocalDateTime minusFiveMinAtNow);
}
