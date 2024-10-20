package hhplus.concertreservationservice.application.concert.facade;

import hhplus.concertreservationservice.application.concert.dto.ConcertCriteria;
import hhplus.concertreservationservice.application.concert.dto.ConcertResult;
import hhplus.concertreservationservice.domain.concert.service.ConcertService;
import hhplus.concertreservationservice.domain.queue.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConcertReservationFacade {

    private final QueueService queueService;
    private final ConcertService concertService;


    @Transactional
    public ConcertResult.ReserveSeat reserveSeat(ConcertCriteria.ReserveSeat criteria) {
        // 대기열 검증
        queueService.verifyQueue(criteria.queueToken());

        // 예약 수행
        return ConcertResult.ReserveSeat.fromInfo(concertService.reserveSeat(criteria.toCommand()));

    }

    @Transactional
    public void expireReservationProcess() {
        concertService.expireReservationProcess();
    }

}
