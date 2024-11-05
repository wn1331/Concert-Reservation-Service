package hhplus.concertreservationservice.application.concert.dto;

import hhplus.concertreservationservice.domain.concert.dto.ConcertCommand;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

public record ConcertCriteria() {

    @Builder
    public record Create(
        String title,
        List<LocalDate> dates,
        Integer seatAmount,
        BigDecimal price

    ){

        public ConcertCommand.Create toCommand() {
            return ConcertCommand.Create.builder()
                .title(title)
                .dates(dates)
                .seatAmount(seatAmount)
                .price(price)
                .build();
        }
    }

    @Builder
    public record GetAvailableSchedules(
        Long concertId
    ) {

        public ConcertCommand.GetAvailableSchedules toCommand() {
            return ConcertCommand.GetAvailableSchedules.builder()
                .concertId(concertId)
                .build();

        }
    }

    @Builder
    public record GetAvailableSeats(
        Long concertScheduleId
    ) {

        public ConcertCommand.GetAvailableSeats toCommand() {
            return ConcertCommand.GetAvailableSeats.builder()
                .concertScheduleId(concertScheduleId)
                .build();
        }
    }

    @Builder
    public record ReserveSeat(
        Long userId,
        Long concertSeatId
    ) {

        public ConcertCommand.ReserveSeat toCommand() {
            return ConcertCommand.ReserveSeat.builder()
                .userId(userId)
                .concertSeatId(concertSeatId)
                .build();
        }
    }

    @Builder
    public record Pay(
        Long reservationId,
        Long userId
    ) {

        public ConcertCommand.Pay toCommand(BigDecimal price) {
            return ConcertCommand.Pay.builder()
                .reservationId(reservationId)
                .price(price)
                .build();
        }
    }

}
