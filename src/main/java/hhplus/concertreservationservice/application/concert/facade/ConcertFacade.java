package hhplus.concertreservationservice.application.concert.facade;

import hhplus.concertreservationservice.application.concert.dto.ConcertCriteria;
import hhplus.concertreservationservice.application.concert.dto.ConcertResult;
import hhplus.concertreservationservice.application.concert.dto.ConcertResult.AvailableSchedules;
import hhplus.concertreservationservice.application.concert.dto.ConcertResult.AvailableSeats;
import hhplus.concertreservationservice.domain.concert.service.ConcertService;
import hhplus.concertreservationservice.domain.queue.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConcertFacade {

    private final ConcertService concertService;
    private final QueueService queueService;

    @Transactional(readOnly = true)
    public ConcertResult.AvailableSchedules getAvailableSchedules(ConcertCriteria.GetAvailableSchedules criteria) {
        // 대기열 검증
        queueService.verifyQueue(criteria.queueToken());
        // 콘서트 스케줄 조회 (날짜가 지나지 않은 것들만)
        return AvailableSchedules.fromInfo(concertService.getAvailableSchedules(criteria.toCommand()));
    }

    @Transactional(readOnly = true)
    public ConcertResult.AvailableSeats getAvailableSeats(ConcertCriteria.GetAvailableSeats criteria) {
        // 대기열 검증
        queueService.verifyQueue(criteria.queueToken());
        // 콘서트 좌석 조회
        return AvailableSeats.fromInfo(concertService.getAvailableSeats(criteria.toCommand()));
    }

    @Transactional
    public ConcertResult.ReserveSeat reserveSeat(ConcertCriteria.ReserveSeat criteria) {
        // 대기열 검증
        queueService.verifyQueue(criteria.queueToken());

        // 예약 수행
        return ConcertResult.ReserveSeat.fromInfo(concertService.reserveSeat(criteria.toCommand()));

    }

    public ConcertResult.Pay pay(ConcertCriteria.Pay criteria){
        // 아래 두 개의 서비스 트랜잭션 분리. (멘토링 반영) + 퍼사드에서 트랜잭션 제거.


        // 대기열 검증 및 PASS된지 5분이 지났다면 제거
        queueService.verifyQueueForPay(criteria.toVerifyQueueCommand());

        // 예약내용 결제
        return ConcertResult.Pay.fromInfo(concertService.payReservation(criteria.toCommand()));
    }


    @Transactional
    public void expireReservationProcess() {
        concertService.expireReservationProcess();
    }
}
