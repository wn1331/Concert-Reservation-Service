package hhplus.concertreservationservice.presentation.queue.dto;

import hhplus.concertreservationservice.application.queue.dto.QueueResult;
import lombok.Builder;

public record QueueResponse() {

    @Builder
    public record Enqueue(
        String token
    ){

        public static Enqueue fromResult(QueueResult.Enqueue enqueue) {
            return Enqueue.builder()
                .token(enqueue.queueToken())
                .build();
        }
    }


    @Builder
    public record Order(
        Long order
    ) {

        public static Order fromResult(QueueResult.Order result) {
            return new Order(result.order());
        }
    }
}
