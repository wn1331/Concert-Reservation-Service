package hhplus.concertreservationservice.domain.queue.service;

import hhplus.concertreservationservice.domain.queue.dto.QueueCommand;
import hhplus.concertreservationservice.domain.queue.dto.QueueCommand.Enqueue;
import hhplus.concertreservationservice.domain.queue.dto.QueueInfo;
import hhplus.concertreservationservice.domain.queue.entity.Queue;
import hhplus.concertreservationservice.domain.queue.entity.QueueStatusType;
import hhplus.concertreservationservice.domain.queue.repository.QueueRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final QueueRepository queueRepository;

    public QueueInfo.Enqueue enqueue(QueueCommand.Enqueue command) {
        String token = UUID.randomUUID().toString();
        Queue queue = Queue.builder()
            .userId(command.userId())
            .token(token)
            .status(QueueStatusType.WAITING)
            .build();

        return QueueInfo.Enqueue.fromEntity(queueRepository.save(queue));
    }

    public boolean existsByUserID(Enqueue command) {
        return queueRepository.existsByUserId(command.userId());
    }

    public QueueInfo.Enqueue findByUserId(Enqueue command) throws Exception {
        return QueueInfo.Enqueue.fromEntity(queueRepository.findByUserId(command.userId())
            .orElseThrow(() -> new Exception("sdsd")));

    }
}
