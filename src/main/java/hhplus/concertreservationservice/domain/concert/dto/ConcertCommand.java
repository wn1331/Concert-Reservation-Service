package hhplus.concertreservationservice.domain.concert.dto;

import hhplus.concertreservationservice.domain.user.entity.UserPointHistoryType;
import java.math.BigDecimal;
import lombok.Builder;

public record ConcertCommand() {

    @Builder
    public record GetAvailableSchedules(
        Long concertId
    ) {

    }

    @Builder
    public record GetAvailableSeats(
        Long concertScheduleId
    ) {


    }

    @Builder
    public record ReserveSeat(
        Long userId,
        Long concertSeatId
    ){

    }


    @Builder
    public record Pay(
        Long reservationId,
        Long userId
    ) {

    }

    @Builder
    public record PayHistory(
        Long userId,
        UserPointHistoryType type,
        BigDecimal amount
    ) {

    }

    @Builder
    public record VerifyQueue(
        String queueToken,
        Long reservationId
    ) {

    }
}
