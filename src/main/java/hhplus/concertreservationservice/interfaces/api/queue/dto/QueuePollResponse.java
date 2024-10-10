package hhplus.concertreservationservice.interfaces.api.queue.dto;

public record QueuePollResponse(
    Long userId,
    String token,
    Long order
) {

}
