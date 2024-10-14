package hhplus.concertreservationservice.presentation.api.concert.dto;

import hhplus.concertreservationservice.domain.concert.entity.PaymentStatusType;
import hhplus.concertreservationservice.domain.concert.entity.ScheduleStatusType;
import hhplus.concertreservationservice.domain.concert.entity.SeatStatusType;
import java.time.LocalDate;
import java.util.List;

public record ConcertResponse() {

    public record Schedules(
        List<ScheduleDetail> schedules
    ){
        public record ScheduleDetail(
            Long scheduleId,
            LocalDate date,
            ScheduleStatusType status
        ) {
        }
    }

    public record Seats(
        List<SeatDetail> concertSeats
    ){
        public record SeatDetail(
            Long seatId,
            Integer seatNo,
            SeatStatusType status
        ){

        }
    }

    public record Reserve(
        Long reservationId
    ){

    }
    public record Pay(
        Long reservationId,
        PaymentStatusType status
    ){

    }

}
