package hhplus.concertreservationservice.domain.concert.event.subscriber;

import static hhplus.concertreservationservice.global.utils.JsonUtil.toJson;

import hhplus.concertreservationservice.domain.concert.event.ConcertPaymentSuccessEvent;
import hhplus.concertreservationservice.domain.concert.event.producer.ConcertPaymentMessageProducer;
import hhplus.concertreservationservice.domain.outbox.dto.OutboxCommand;
import hhplus.concertreservationservice.domain.outbox.entity.Outbox.OutboxType;
import hhplus.concertreservationservice.domain.outbox.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventSubscriber {

    @Value("${spring.kafka.producer.topic.payment-success}")
    private String paymentSuccessTopic;


    private final OutboxService outboxService;
    private final ConcertPaymentMessageProducer concertPaymentMessageProducer;

    // 커밋이 완료된 시점에 수행
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendNotification(ConcertPaymentSuccessEvent event) {
        // 아웃박스 저장
        String jsonEvent = toJson(event);
        String outboxId = outboxService.saveOutboxAndGetId(OutboxCommand.Create.builder()
            .topic(paymentSuccessTopic)
            .key(event.paymentId().toString())
            .payload(jsonEvent)
            .type(OutboxType.PAYMENT)
            .build());

        concertPaymentMessageProducer.sendSuccessMessage(event.paymentId().toString(), outboxId);
    }


}
