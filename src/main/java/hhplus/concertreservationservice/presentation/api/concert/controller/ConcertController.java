package hhplus.concertreservationservice.presentation.api.concert.controller;

import static org.springframework.http.ResponseEntity.ok;

import hhplus.concertreservationservice.domain.concert.entity.PaymentStatusType;
import hhplus.concertreservationservice.domain.concert.entity.ScheduleStatusType;
import hhplus.concertreservationservice.domain.concert.entity.SeatStatusType;

import hhplus.concertreservationservice.presentation.api.concert.dto.ConcertRequest;
import hhplus.concertreservationservice.presentation.api.concert.dto.ConcertResponse;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/concerts")
public class ConcertController {


    // 콘서트 스케줄(날짜) 조회 API
    @GetMapping("/{concertId}/schedules")
    public ResponseEntity<ConcertResponse.Schedules> getConcertSchedules(
        @PathVariable(name = "concertId") Long concertScheduleId,
        @RequestHeader(name = "token") String token

    ){

        return ok(null);
    }

    // 콘서트 좌석 조회 API
    @GetMapping("/schedules/{concertScheduleId}/seats")
    public ResponseEntity<ConcertResponse.Seats> getConcertSeats(
        @PathVariable(name = "concertScheduleId") Long concertScheduleId,
        @RequestHeader(name = "token") String token

    ){


        return ok(null);
    }

    // 콘서트 예약 API
    @PostMapping("/reservation")
    public ResponseEntity<ConcertResponse.Reserve> reserveConcert(
        @RequestBody @Valid ConcertRequest.Reserve request,
        @RequestHeader(name = "token") String token){

        return ok(null);

    }

    // 콘서트 좌석 결제 API
    @PostMapping("/reservations/{reservationId}/pay")
    public ResponseEntity<ConcertResponse.Pay> payConcert(
        @PathVariable(name = "reservationId") Long reservationId,
        @RequestHeader(name = "token") String token,
        @RequestBody @Valid ConcertRequest.Pay request
    ){
        return ok(null);
    }






}
