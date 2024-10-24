package hhplus.concertreservationservice.presentation.concert.controller;

import static org.springframework.http.ResponseEntity.ok;

import hhplus.concertreservationservice.application.concert.dto.ConcertCriteria;
import hhplus.concertreservationservice.application.concert.dto.ConcertCriteria.GetAvailableSchedules;
import hhplus.concertreservationservice.application.concert.dto.ConcertCriteria.GetAvailableSeats;
import hhplus.concertreservationservice.application.concert.facade.ConcertFacade;
import hhplus.concertreservationservice.application.concert.facade.ConcertPaymentFacade;
import hhplus.concertreservationservice.application.concert.facade.ConcertReservationFacade;
import hhplus.concertreservationservice.presentation.concert.dto.ConcertRequest;
import hhplus.concertreservationservice.presentation.concert.dto.ConcertResponse;
import hhplus.concertreservationservice.presentation.concert.dto.ConcertResponse.AvailableSchedules;
import hhplus.concertreservationservice.presentation.concert.dto.ConcertResponse.AvailableSeats;
import hhplus.concertreservationservice.presentation.concert.dto.ConcertResponse.ReserveSeat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ConcertController implements IConcertController{

    private final ConcertFacade concertFacade;
    private final ConcertReservationFacade concertReservationFacade;
    private final ConcertPaymentFacade concertPaymentFacade;


    // 콘서트 스케줄(날짜) 조회 API
    @GetMapping("/{concertId}/schedules")
    public ResponseEntity<ConcertResponse.AvailableSchedules> getConcertSchedules(
        @PathVariable(name = "concertId") Long concertId,
        @RequestHeader(name = "X-Access-Token") String queueToken

    ) {

        return ok(AvailableSchedules.fromResult(
            concertFacade.getAvailableSchedules(new ConcertCriteria.GetAvailableSchedules(concertId, queueToken))));
    }

    // 콘서트 좌석 조회 API
    @GetMapping("/schedules/{concertScheduleId}/seats")
    public ResponseEntity<AvailableSeats> getConcertSeats(
        @PathVariable(name = "concertScheduleId") Long concertScheduleId,
        @RequestHeader(name = "X-Access-Token") String queueToken

    ) {

        return ok(ConcertResponse.AvailableSeats.fromResult(
            concertFacade.getAvailableSeats(new GetAvailableSeats(concertScheduleId, queueToken))));
    }

    // 콘서트 예약 API
    @PostMapping("/reservation")
    public ResponseEntity<ReserveSeat> reserveConcert(
        @RequestBody @Valid ConcertRequest.ReserveSeat request,
        @RequestHeader(name = "X-Access-Token") String queueToken) {

        return ok(ConcertResponse.ReserveSeat.fromResult(concertReservationFacade.reserveSeat(
            request.toCriteria(queueToken))));

    }

    // 콘서트 좌석 결제 API
    @PostMapping("/reservations/{reservationId}/pay")
    public ResponseEntity<ConcertResponse.Pay> payConcert(
        @PathVariable(name = "reservationId") Long reservationId,
        @RequestHeader(name = "X-Access-Token") String queueToken,
        @RequestBody @Valid ConcertRequest.Pay request
    ){
        return ok(ConcertResponse.Pay.fromResult(concertPaymentFacade.pay(request.toCriteria(reservationId,queueToken))));
    }


}
