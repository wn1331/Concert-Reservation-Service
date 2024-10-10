package hhplus.concertreservationservice.interfaces.api.concert.dto;

import jakarta.validation.constraints.NotBlank;

public record ConcertReservation() {

    public record Request(
        @NotBlank Long userId,
        @NotBlank Long concertScheduleId,
        @NotBlank Long concertSeatId
    ) {

    }

    public record Response(
        Long reservationId
    ) {

    }


}
