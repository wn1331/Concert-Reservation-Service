package hhplus.concertreservationservice.domain.queue.dto;

import lombok.Builder;

public record QueueCommand(
) {
    @Builder
    public record Enqueue(
        Long userId
    ){
    }

}
