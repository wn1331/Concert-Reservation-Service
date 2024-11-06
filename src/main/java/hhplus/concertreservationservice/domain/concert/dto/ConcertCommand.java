package hhplus.concertreservationservice.domain.concert.dto;

import hhplus.concertreservationservice.domain.user.entity.UserPointHistoryType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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
        BigDecimal price
    ) {

    }

    @Builder
    public record Create(
        String title,
        List<LocalDate> dates,
        Integer seatAmount,
        BigDecimal price
    ) {

    }
}
