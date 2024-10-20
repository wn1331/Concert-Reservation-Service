package hhplus.concertreservationservice.application.concert.dto;

import hhplus.concertreservationservice.domain.concert.dto.ConcertCommand;
import java.math.BigDecimal;
import lombok.Builder;

public record ConcertCriteria() {

    @Builder
    public record GetAvailableSchedules(
        Long concertId,
        String queueToken
    ) {

        public ConcertCommand.GetAvailableSchedules toCommand() {
            return ConcertCommand.GetAvailableSchedules.builder()
                .concertId(concertId)
                .build();

        }
    }

    @Builder
    public record GetAvailableSeats(
        Long concertScheduleId,
        String queueToken
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
        Long concertSeatId,
        String queueToken
    ) {

        public ConcertCommand.ReserveSeat toCommand(BigDecimal price) {
            return ConcertCommand.ReserveSeat.builder()
                .userId(userId)
                .concertSeatId(concertSeatId)
                .price(price)
                .build();
        }
    }

    @Builder
    public record Pay(
        Long reservationId,
        String queueToken,
        Long userId
    ) {

        public ConcertCommand.Pay toCommand() {
            return ConcertCommand.Pay.builder()
                .reservationId(reservationId)
                .userId(userId)
                .build();
        }
        public ConcertCommand.VerifyQueue toVerifyQueueCommand(){
            return ConcertCommand.VerifyQueue.builder()
               .queueToken(queueToken)
               .reservationId(reservationId)
               .build();
        }
    }

}
