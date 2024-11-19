package hhplus.concertreservationservice.application.concert.facade;

import hhplus.concertreservationservice.application.concert.dto.ConcertCriteria;
import hhplus.concertreservationservice.domain.concert.event.ConcertPaymentSuccessEvent;
import hhplus.concertreservationservice.application.concert.dto.ConcertResult;
import hhplus.concertreservationservice.domain.concert.event.publisher.ConcertPaymentEventPublisher;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo.ReservationStatusInfo;
import hhplus.concertreservationservice.domain.concert.service.ConcertPaymentService;
import hhplus.concertreservationservice.domain.concert.service.ConcertReservationService;
import hhplus.concertreservationservice.domain.concert.service.ConcertService;
import hhplus.concertreservationservice.domain.queue.service.QueueService;
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
    private final QueueService queueService;
    private final ConcertPaymentEventPublisher paymentEventPublisher;

    @Transactional
    public ConcertResult.Pay pay(ConcertCriteria.Pay criteria) {
        // 예약 상태 변경.
        ReservationStatusInfo reservationStatusInfo = concertReservationService.changeReservationStatusPaid(
            criteria.reservationId());

        // 유저 잔액 차감
        // 히스토리 저장은 event로 실행 (외부 데이터 플랫폼이라 가정, 이벤트 발행으로 변경)
        userService.userPayReservation(UserCommand.UserPay.builder()
            .userId(criteria.userId())
            .price(reservationStatusInfo.price())
            .build()
        );

        // 좌석 상태 변경.
        concertService.changeSeatStatusPaid(reservationStatusInfo.concertSeatId());

        // 예약내용 결제
        ConcertInfo.Pay pay = concertPaymentService.payReservation(
            criteria.toCommand(reservationStatusInfo.price()));

        // 대기열 만료처리. 이미 대기열이 만료가 되어있더라도 Exception이 발생하지는 않음.
        queueService.expireToken(criteria.token());

        // 결제완료 이벤트 발행
        paymentEventPublisher.success(ConcertPaymentSuccessEvent.builder()
                .userId(criteria.userId())
                .price(reservationStatusInfo.price())
                .reservationId(reservationStatusInfo.reservationId())
                .paymentId(pay.paymentId())
            .build());

        return ConcertResult.Pay.fromInfo(pay);
    }

}
