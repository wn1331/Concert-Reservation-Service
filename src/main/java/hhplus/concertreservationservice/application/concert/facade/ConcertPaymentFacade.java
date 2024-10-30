package hhplus.concertreservationservice.application.concert.facade;

import hhplus.concertreservationservice.application.concert.dto.ConcertCriteria;
import hhplus.concertreservationservice.application.concert.dto.ConcertResult;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo.ReservationStatus;
import hhplus.concertreservationservice.domain.concert.service.ConcertPaymentService;
import hhplus.concertreservationservice.domain.concert.service.ConcertReservationService;
import hhplus.concertreservationservice.domain.concert.service.ConcertService;
import hhplus.concertreservationservice.domain.user.dto.UserCommand;
import hhplus.concertreservationservice.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConcertPaymentFacade {

    private final UserService userService;
    private final ConcertService concertService;
    private final ConcertReservationService concertReservationService;
    private final ConcertPaymentService concertPaymentService;

    @Transactional
    public ConcertResult.Pay pay(ConcertCriteria.Pay criteria) {
        // 예약내용 결제
        // 예약 상태 변경.
        ReservationStatus reservationStatus = concertReservationService.changeReservationStatusPaid(
            criteria.reservationId());

        // 유저 잔액 차감 및 히스토리 저장.
        userService.userPayReservation(UserCommand.UserPay.builder()
            .userId(criteria.userId())
            .price(reservationStatus.price())
            .build()
        );

        // 좌석 상태 변경.
        concertService.changeSeatStatusPaid(reservationStatus.concertSeatId());

        // 예약내용 결제
        ConcertInfo.Pay pay = concertPaymentService.payReservation(criteria.toCommand(reservationStatus.price()));

        return ConcertResult.Pay.fromInfo(pay);    }

}
