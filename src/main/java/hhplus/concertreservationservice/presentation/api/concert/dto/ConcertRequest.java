package hhplus.concertreservationservice.presentation.api.concert.dto;

import jakarta.validation.constraints.NotNull;

public record ConcertRequest() {

    public record Reserve(
        Long userId,
        Long concertScheduleId,
        Long concertSeatId
    ){

    }

    public record Pay(
        @NotNull
        Long userId
    ){

    }

}
