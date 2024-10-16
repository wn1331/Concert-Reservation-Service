package hhplus.concertreservationservice.presentation.queue.dto;

import hhplus.concertreservationservice.application.queue.dto.QueueResult;
import lombok.Builder;

public record QueueResponse() {

    @Builder
    public record Enqueue(
        String token,
        Long order
    ){

        public static Enqueue fromResult(QueueResult.Enqueue enqueue) {
            return Enqueue.builder()
                .token(enqueue.queueToken())
                .order(enqueue.order())
                .build();
        }
    }


}
