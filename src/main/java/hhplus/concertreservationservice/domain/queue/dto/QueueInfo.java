package hhplus.concertreservationservice.domain.queue.dto;

import hhplus.concertreservationservice.domain.queue.entity.Queue;
import lombok.Builder;

public record QueueInfo() {

    @Builder
    public record Enqueue(
        String queueToken,
        Long order
    ){

        public static QueueInfo.Enqueue fromEntity(Queue queue, Long order){
            return Enqueue.builder()
                .queueToken(queue.getQueueToken())
                .order(order)
                .build();
        }

    }

    public record Order(Long id) {

    }
}
