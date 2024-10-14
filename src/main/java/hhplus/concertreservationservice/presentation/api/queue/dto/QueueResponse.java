package hhplus.concertreservationservice.presentation.api.queue.dto;

import hhplus.concertreservationservice.application.queue.dto.QueueResult;
import hhplus.concertreservationservice.application.queue.dto.QueueResult.Enqueue;
import lombok.Builder;

public record QueueResponse() {

    @Builder
    public record Enqueue(
        String token
    ){

        public static Enqueue fromResult(QueueResult.Enqueue enqueue) {
            return Enqueue.builder()
                .token(enqueue.token())
                .build();
        }
    }

    public record Poll(
        Long userId,
        String token,
        Long order
    ) {

    }


}
