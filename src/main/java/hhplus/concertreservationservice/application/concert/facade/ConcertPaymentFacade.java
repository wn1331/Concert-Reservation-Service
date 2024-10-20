package hhplus.concertreservationservice.application.concert.facade;

import hhplus.concertreservationservice.application.concert.dto.ConcertCriteria;
import hhplus.concertreservationservice.application.concert.dto.ConcertResult;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo;
import hhplus.concertreservationservice.domain.concert.service.ConcertPaymentService;
import hhplus.concertreservationservice.domain.concert.service.ConcertService;
import hhplus.concertreservationservice.domain.queue.service.QueueService;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConcertPaymentFacade {

    private final QueueService queueService;
    private final ConcertPaymentService concertPaymentService;

    public ConcertResult.Pay pay(ConcertCriteria.Pay criteria){

        // 예약내용 결제 (트랜잭션 적용. 위 서비스와 아래 서비스를 트랜잭션 분리 - 멘토링 반영)
        ConcertInfo.Pay pay = concertPaymentService.payReservation(criteria.toCommand());

        return ConcertResult.Pay.fromInfo(pay);
    }

}
