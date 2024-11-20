package hhplus.concertreservationservice.application.concert.facade;

import hhplus.concertreservationservice.application.concert.dto.ConcertCriteria;
import hhplus.concertreservationservice.domain.concert.event.ConcertPaymentSuccessEvent;
import hhplus.concertreservationservice.application.concert.dto.ConcertResult;
import hhplus.concertreservationservice.domain.concert.event.producer.ConcertPaymentMessageProducer;
import hhplus.concertreservationservice.domain.concert.event.publisher.ConcertPaymentEventPublisher;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo.ReservationStatusInfo;
import hhplus.concertreservationservice.domain.concert.service.ConcertPaymentService;
import hhplus.concertreservationservice.domain.concert.service.ConcertReservationService;
import hhplus.concertreservationservice.domain.concert.service.ConcertService;
import hhplus.concertreservationservice.domain.outbox.entity.Outbox;
import hhplus.concertreservationservice.domain.outbox.service.OutboxService;
import hhplus.concertreservationservice.domain.user.dto.UserCommand;
import hhplus.concertreservationservice.domain.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConcertPaymentFacade {

    private final UserService userService;
    private final ConcertService concertService;
    private final ConcertReservationService concertReservationService;
    private final ConcertPaymentService concertPaymentService;
    private final OutboxService outboxService;
    private final ConcertPaymentMessageProducer concertPaymentMessageProducer;
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

        // 대기열 만료처리도 이벤트로 이관. kafka consumer가 수행한다.

        // 결제완료 이벤트 발행
        paymentEventPublisher.success(ConcertPaymentSuccessEvent.builder()
                .userId(criteria.userId())
                .price(reservationStatusInfo.price())
                .reservationId(reservationStatusInfo.reservationId())
                .paymentId(pay.paymentId())
                .token(criteria.token())
            .build());

        return ConcertResult.Pay.fromInfo(pay);
    }

    public void retryMessage() {
        List<Outbox> outboxesForRetry = outboxService.findFailedOutbox();
        log.info("스케줄러 : {}개의 아웃박스 재발행 시작....", outboxesForRetry.size());
        outboxesForRetry.forEach(outbox -> {
            try {
                concertPaymentMessageProducer.sendSuccessMessage(outbox.getKey(),outbox.getId());
            }catch (Exception e) {
                log.error("아웃박스 재발행 시 오류 발생. outboxId : {}",outbox.getId());
            }
        });
        log.info("아웃박스 재발행 스케줄러 완료..");
    }
}
