package hhplus.concertreservationservice.interfaces.api.concert.dto;

public record ConcertReservation() {

    public record Request(
        Long userId,
        Long concertScheduleId,
        Long concertSeatId
    ) {

    }

    public record Response(
        Long reservationId
    ) {

    }


}
