package hhplus.concertreservationservice.application.concert.facade;

import hhplus.concertreservationservice.application.concert.dto.ConcertCriteria;
import hhplus.concertreservationservice.application.concert.dto.ConcertResult;
import hhplus.concertreservationservice.domain.concert.service.ConcertPaymentService;
import hhplus.concertreservationservice.global.aspect.RedissionPubSubLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConcertPaymentFacade {

    private final ConcertPaymentService concertPaymentService;

    @RedissionPubSubLock(value = "'payReservationId-' + #criteria.reservationId", waitTime = 30, leaseTime = 10)
    public ConcertResult.Pay pay(ConcertCriteria.Pay criteria) {
        // 예약내용 결제
        return ConcertResult.Pay.fromInfo(concertPaymentService.payReservation(criteria.toCommand()));
    }

}
