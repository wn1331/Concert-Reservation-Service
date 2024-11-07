package hhplus.concertreservationservice.application.concert.facade;

import hhplus.concertreservationservice.application.concert.dto.ConcertCriteria;
import hhplus.concertreservationservice.application.concert.dto.ConcertResult;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo.ReserveSeat;
import hhplus.concertreservationservice.domain.concert.service.ConcertReservationService;
import hhplus.concertreservationservice.global.aspect.RedissionPubSubLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConcertReservationFacade {

    private final ConcertReservationService concertReservationService;

    @RedissionPubSubLock(value = "'reserveSeatId-' + #criteria.concertSeatId", waitTime = 30, leaseTime = 10)
    public ConcertResult.ReserveSeat reserveSeat(ConcertCriteria.ReserveSeat criteria) {

        // 좌석 관련 서비스 +  예약 서비스 호출(합치고, 서비스에 트랜잭션이 걸려있어야 동작한다.)
        ReserveSeat reserveSeatInfo = concertReservationService.reserveSeat(
            criteria.toCommand());

        return ConcertResult.ReserveSeat.fromInfo(reserveSeatInfo);


    }


    // 예약 만료 스케줄러.
    @Transactional
    public void expireReservationProcess() {
        concertReservationService.expireReservationProcess();
    }

}
