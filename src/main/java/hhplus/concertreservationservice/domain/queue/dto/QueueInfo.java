package hhplus.concertreservationservice.domain.queue.dto;

import hhplus.concertreservationservice.domain.queue.entity.Queue;
import lombok.Builder;

public record QueueInfo() {

    @Builder
    public record Enqueue(
        String token
    ){

        public static QueueInfo.Enqueue fromEntity(Queue queue){
            return Enqueue.builder()
                .token(queue.getToken())
                .build();
        }

    }

}
