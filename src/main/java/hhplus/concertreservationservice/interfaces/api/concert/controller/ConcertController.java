package hhplus.concertreservationservice.interfaces.api.concert.controller;

import static org.springframework.http.ResponseEntity.ok;

import hhplus.concertreservationservice.domain.concert.entity.PaymentStatusType;
import hhplus.concertreservationservice.domain.concert.entity.ScheduleStatusType;
import hhplus.concertreservationservice.domain.concert.entity.SeatStatusType;
import hhplus.concertreservationservice.interfaces.api.concert.dto.ConcertPay;
import hhplus.concertreservationservice.interfaces.api.concert.dto.ConcertReservation;
import hhplus.concertreservationservice.interfaces.api.concert.dto.ConcertSchedules;

import hhplus.concertreservationservice.interfaces.api.concert.dto.ConcertSeats;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/concert")
public class ConcertController {


    // 콘서트 스케줄(날짜) 조회 API
    @GetMapping("/{concertId}/schedules")
    public ResponseEntity<ConcertSchedules.Response> getConcertSchedules(
        @PathVariable(name = "concertId") Long concertScheduleId,
        @RequestHeader(name = "token") String token

    ){
        List<ConcertSchedules.Response.ConcertScheduleResponse> mockSchedules = Arrays.asList(
            new ConcertSchedules.Response.ConcertScheduleResponse(1L, LocalDate.of(2024, 10, 1), ScheduleStatusType.SOLD_OUT),
            new ConcertSchedules.Response.ConcertScheduleResponse(2L, LocalDate.of(2024, 10, 2), ScheduleStatusType.AVAILABLE),
            new ConcertSchedules.Response.ConcertScheduleResponse(3L, LocalDate.of(2024, 10, 3), ScheduleStatusType.AVAILABLE)
        );

        return ok(new ConcertSchedules.Response(mockSchedules));
    }

    // 콘서트 좌석 조회 API
    @GetMapping("/schedule/{concertScheduleId}/seats")
    public ResponseEntity<ConcertSeats.Response> getConcertSeats(
        @PathVariable(name = "concertScheduleId") Long concertScheduleId,
        @RequestHeader(name = "token") String token

    ){
        List<ConcertSeats.Response.ConcertSeatResponse> mockSeats = Arrays.asList(
            new ConcertSeats.Response.ConcertSeatResponse(1L, 101, SeatStatusType.EMPTY),
            new ConcertSeats.Response.ConcertSeatResponse(2L, 102, SeatStatusType.RESERVED),
            new ConcertSeats.Response.ConcertSeatResponse(3L, 103, SeatStatusType.EMPTY),
            new ConcertSeats.Response.ConcertSeatResponse(4L, 104, SeatStatusType.SOLD)
        );

        // ConcertSeatsResponse 객체 반환
        return ok(new ConcertSeats.Response(mockSeats));
    }

    // 콘서트 예약 API
    @PostMapping("/reservation")
    public ResponseEntity<ConcertReservation.Response> reserveConcert(
        @RequestBody @Valid ConcertReservation.Request request,
        @RequestHeader(name = "token") String token){

        return ok(new ConcertReservation.Response(1L));

    }

    // 콘서트 좌석 결제 API
    @PostMapping("/reservation/{reservationId}/pay")
    public ResponseEntity<ConcertPay.Response> payConcert(
        @PathVariable(name = "reservationId") Long reservationId,
        @RequestHeader(name = "token") String token,
        @RequestBody @Valid ConcertPay.Request request
    ){
        return ok(new ConcertPay.Response(1L, PaymentStatusType.SUCCEED));
    }






}
