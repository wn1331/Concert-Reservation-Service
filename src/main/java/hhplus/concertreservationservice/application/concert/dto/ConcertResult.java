package hhplus.concertreservationservice.application.concert.dto;

import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo.Pay;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo.ReserveSeat;
import hhplus.concertreservationservice.domain.concert.entity.ScheduleStatusType;
import hhplus.concertreservationservice.domain.concert.entity.SeatStatusType;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

public record ConcertResult() {

    public record Create(
        Long id
    ){
        public static Create fromInfo(ConcertInfo.Create info){
            return new Create(info.id());
        }
    }

    @Builder
    public record AvailableSchedules(List<ScheduleDetail> schedules) {

        @Builder
        public record ScheduleDetail(Long id, LocalDate date, ScheduleStatusType status) {

        }

        public static AvailableSchedules fromInfo(
            ConcertInfo.AvailableSchedules availableSchedules) {

            return AvailableSchedules.builder()
                .schedules(availableSchedules.schedules().stream()
                    .map(infoDetail -> ScheduleDetail.builder()
                        .id(infoDetail.id())
                        .date(infoDetail.date())
                        .status(infoDetail.status())
                        .build())
                    .toList())
                .build();
        }


    }

    @Builder
    public record AvailableSeats(List<SeatDetail> seats) {

        @Builder
        public record SeatDetail(Long id, String seatNum, SeatStatusType status) {

        }

        public static AvailableSeats fromInfo(ConcertInfo.AvailableSeats availableSeats) {
            return ConcertResult.AvailableSeats.builder()
                .seats(availableSeats.seats().stream()
                    .map(infoDetail -> SeatDetail.builder()
                        .id(infoDetail.id())
                        .seatNum(infoDetail.seatNum())
                        .status(infoDetail.status())
                        .build())
                    .toList())
                .build();
        }

    }

    @Builder
    public record ReserveSeat(
        Long reservationId
    ){

        public static ReserveSeat fromInfo(ConcertInfo.ReserveSeat reserveSeat) {
            return ReserveSeat.builder()
                .reservationId(reserveSeat.reservationId())
                .build();

        }
    }

    @Builder
    public record Pay(
        Long paymentId
    ) {
        public static Pay fromInfo(ConcertInfo.Pay pay) {
            return Pay.builder()
               .paymentId(pay.paymentId())
               .build();
        }
    }

    public record Concerts(
        Long id,
        String title

    ) {


    }

    public record GetConcertList(List<ConcertResult.Concerts> concertsResultList) {

    }
}
