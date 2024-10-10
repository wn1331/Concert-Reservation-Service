package hhplus.concertreservationservice.interfaces.api.concert.controller;

import static org.springframework.http.ResponseEntity.ok;

import hhplus.concertreservationservice.interfaces.api.concert.dto.ConcertPay;
import hhplus.concertreservationservice.interfaces.api.concert.dto.ConcertReservation;
import hhplus.concertreservationservice.interfaces.api.concert.dto.ConcertSchedulesResponse;
import hhplus.concertreservationservice.interfaces.api.concert.dto.ConcertSchedulesResponse.ConcertScheduleResponse;
import hhplus.concertreservationservice.interfaces.api.concert.dto.ConcertSeatsResponse;
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
    public ResponseEntity<ConcertSchedulesResponse> getConcertSchedules(
        @PathVariable(name = "concertId") Long concertScheduleId,
        @RequestHeader(name = "token") String token

    ){
        List<ConcertScheduleResponse> mockSchedules = Arrays.asList(
            new ConcertScheduleResponse(1L, LocalDate.of(2024, 10, 1), "매진됨"),
            new ConcertScheduleResponse(2L, LocalDate.of(2024, 10, 2), "예약가능"),
            new ConcertScheduleResponse(3L, LocalDate.of(2024, 10, 3), "예약가능")
        );

        return ok(new ConcertSchedulesResponse(mockSchedules));
    }

    // 콘서트 좌석 조회 API
    @GetMapping("/schedule/{concertScheduleId}/seats")
    public ResponseEntity<ConcertSeatsResponse> getConcertSeats(
        @PathVariable(name = "concertScheduleId") Long concertScheduleId,
        @RequestHeader(name = "token") String token

    ){
        List<ConcertSeatsResponse.ConcertSeatResponse> mockSeats = Arrays.asList(
            new ConcertSeatsResponse.ConcertSeatResponse(1L, 101, "예약가능"),
            new ConcertSeatsResponse.ConcertSeatResponse(2L, 102, "예약가능"),
            new ConcertSeatsResponse.ConcertSeatResponse(3L, 103, "예약가능"),
            new ConcertSeatsResponse.ConcertSeatResponse(4L, 104, "예약불가")
        );

        // ConcertSeatsResponse 객체 반환
        return ok(new ConcertSeatsResponse(mockSeats));
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
        return ok(new ConcertPay.Response(1L,"결제성공"));
    }






}
