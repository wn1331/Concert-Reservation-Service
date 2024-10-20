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

        // 대기열 검증 및 PASS된지 5분이 지났다면 제거 + 예약 만료 확인 (변경감지를 위한 트랜잭션 적용)
        boolean isTimeOutException = queueService.verifyQueueForPay(criteria.toVerifyQueueCommand());

        // 어쩔 수 없는 Exception (위 서비스에서 예외 터져버리면 롤백되므로 예외를 바깥으로 뺌. transactional을 제거하면 더티체킹 사용 불가)
        if(isTimeOutException){
            throw new CustomGlobalException(ErrorCode.RESERVATION_TIMEOUT);
        }

        // 예약내용 결제 (트랜잭션 적용. 위 서비스와 아래 서비스를 트랜잭션 분리 - 멘토링 반영)
        ConcertInfo.Pay pay = concertPaymentService.payReservation(criteria.toCommand());

        // 요구사항 : 결제가 완료되면 대기열 토큰을 만료시킵니다.
        queueService.expireToken(criteria.queueToken());

        return ConcertResult.Pay.fromInfo(pay);
    }

}
