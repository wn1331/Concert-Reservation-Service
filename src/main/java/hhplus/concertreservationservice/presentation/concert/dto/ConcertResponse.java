package hhplus.concertreservationservice.presentation.concert.dto;

import hhplus.concertreservationservice.application.concert.dto.ConcertResult;
import hhplus.concertreservationservice.domain.concert.entity.ScheduleStatusType;
import hhplus.concertreservationservice.domain.concert.entity.SeatStatusType;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

public record ConcertResponse() {

    public record Concerts(
        Long id,
        String title
    ) {

    }

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

        public static AvailableSchedules fromResult(
            ConcertResult.AvailableSchedules availableSchedules) {
            return AvailableSchedules.builder()
                .schedules(availableSchedules.schedules().stream()
                    .map(resultDetail -> ScheduleDetail.builder()
                        .id(resultDetail.id())
                        .date(resultDetail.date())
                        .status(resultDetail.status())
                        .build())
                    .toList())
                .build();
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

        public static ConcertResponse.AvailableSeats fromResult(
            ConcertResult.AvailableSeats availableSeats) {
            return AvailableSeats.builder()
                .seats(availableSeats.seats().stream()
                    .map(resultDetail -> SeatDetail.builder()
                        .id(resultDetail.id())
                        .seatNum(resultDetail.seatNum())
                        .status(resultDetail.status())
                        .build())
                    .toList())
                .build();
        }
    }


    @Builder
    public record ReserveSeat(
        Long reservationId
    ) {

        public static ConcertResponse.ReserveSeat fromResult(
            ConcertResult.ReserveSeat reserveSeat) {
            return ReserveSeat.builder()
                .reservationId(reserveSeat.reservationId())
                .build();
        }

    }

    @Builder
    public record Pay(
        Long paymentId
    ) {

        public static ConcertResponse.Pay fromResult(ConcertResult.Pay result) {
            return ConcertResponse.Pay.builder()
                .paymentId(result.paymentId())
                .build();
        }
    }

    @Builder
    public record Create(
        Long id
    ) {

        public static ConcertResponse.Create fromResult(ConcertResult.Create result) {
            return ConcertResponse.Create.builder().id(result.id()).build();
        }
    }

    public record GetConcertList(
        List<ConcertResponse.Concerts> concertsList
    ) {

    }
}
