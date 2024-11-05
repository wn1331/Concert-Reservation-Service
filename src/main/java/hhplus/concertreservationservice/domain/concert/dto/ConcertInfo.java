package hhplus.concertreservationservice.domain.concert.dto;

import hhplus.concertreservationservice.domain.concert.entity.ConcertPayment;
import hhplus.concertreservationservice.domain.concert.entity.ConcertReservation;
import hhplus.concertreservationservice.domain.concert.entity.ConcertSchedule;
import hhplus.concertreservationservice.domain.concert.entity.ConcertSeat;
import hhplus.concertreservationservice.domain.concert.entity.ReservationStatusType;
import hhplus.concertreservationservice.domain.concert.entity.ScheduleStatusType;
import hhplus.concertreservationservice.domain.concert.entity.SeatStatusType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

public record ConcertInfo() {

    @Builder
    public record AvailableSchedules(
        List<ScheduleDetail> schedules
    ) {

        @Builder
        public record ScheduleDetail(
            Long id,
            LocalDate date,
            ScheduleStatusType status
        ) {

        }

        public static ConcertInfo.AvailableSchedules fromEntityList(
            List<ConcertSchedule> scheduleList) {
            return new ConcertInfo.AvailableSchedules(scheduleList.stream().map(schedule ->
                ScheduleDetail.builder().id(
                        schedule.getId()).date(schedule.getConcertDate())
                    .status(ScheduleStatusType.AVAILABLE).build()
            ).toList());
        }
    }

    @Builder
    public record AvailableSeats(
        List<SeatDetail> seats
    ) {


        @Builder
        public record SeatDetail(
            Long id,
            String seatNum,
            SeatStatusType status
        ) {

        }

        public static AvailableSeats fromEntityList(List<ConcertSeat> seatList) {
            return new AvailableSeats(seatList.stream().map(seat ->
                SeatDetail.builder().id(seat.getId()).seatNum(seat.getSeatNum())
                    .status(seat.getStatus()).build()
            ).toList());
        }

    }

    @Builder
    public record ReserveSeat(
        Long reservationId
    ) {

    }

    @Builder
    public record GetReservation(
        Long userId,
        Long reservationId,
        BigDecimal price,
        ReservationStatusType status
    ) {

        public static GetReservation fromEntity(ConcertReservation concertReservation) {
            return GetReservation.builder()
                .userId(concertReservation.getUserId())
               .reservationId(concertReservation.getId())
                .price(concertReservation.getPrice())
                .status(concertReservation.getStatus())
                .build();
        }
    }

    @Builder
    public record Pay(
        Long paymentId
    ) {
        public static Pay fromEntity(ConcertPayment concertPayment) {
            return Pay.builder()
               .paymentId(concertPayment.getId())
               .build();
        }

    }

    @Builder
    public record ReservationStatus(
        Long reservationId,
        Long concertSeatId,
        BigDecimal price
    ){
    }

    public record Create(
        Long id
    ) {

    }

    public record Concert(
        Long id,
        String title
    ) {

    }
}
