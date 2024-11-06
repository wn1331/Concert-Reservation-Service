package hhplus.concertreservationservice.domain.queue.dto;

import lombok.Builder;

public record QueueInfo() {

    @Builder
    public record Enqueue(
        String queueToken,
        Long order
    ){

    }


}
