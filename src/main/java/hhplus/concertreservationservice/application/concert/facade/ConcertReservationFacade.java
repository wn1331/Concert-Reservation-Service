package hhplus.concertreservationservice.application.concert.facade;

import hhplus.concertreservationservice.application.concert.dto.ConcertCriteria;
import hhplus.concertreservationservice.application.concert.dto.ConcertResult;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo.ReserveSeat;
import hhplus.concertreservationservice.domain.concert.service.ConcertReservationService;
import hhplus.concertreservationservice.domain.concert.service.ConcertService;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ConcertReservationFacade {

    private final ConcertReservationService concertReservationService;
    private final ConcertService concertService;


    @Transactional
    public ConcertResult.ReserveSeat reserveSeat(ConcertCriteria.ReserveSeat criteria) {

        // 좌석 관련 로직 서비스 호출
        BigDecimal seatPrice = concertService.changeSeatStatusAndReturnPrice(
            criteria.concertSeatId());

        // 예약 서비스 호출
        ReserveSeat reserveSeatInfo = concertReservationService.reserveSeat(
            criteria.toCommand(seatPrice));

        return ConcertResult.ReserveSeat.fromInfo(reserveSeatInfo);


    }


    // 예약 만료 스케줄러.
    @Transactional
    public void expireReservationProcess() {
        concertReservationService.expireReservationProcess();
    }

}
