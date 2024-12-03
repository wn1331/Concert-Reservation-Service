package hhplus.concertreservationservice.domain.queue.dto;

import lombok.Builder;

public record QueueCommand(
) {
    @Builder
    public record Enqueue(
        Long userId
    ){
    }

    @Builder
    public record VerifyQueue(
        String queueToken
    ) {

    }

    @Builder
    public record VerifyQueueForPay(
        String queueToken,
        Long reservationId
    ) {

    }

    public record Order(String token) {

    }
}
