package hhplus.concertreservationservice.interfaces.api.queue.dto;

public record QueuePoll() {

    public record Response(
        Long userId,
        String token,
        Long order
    ) {

    }
}
