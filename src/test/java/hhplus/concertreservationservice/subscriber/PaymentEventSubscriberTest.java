package hhplus.concertreservationservice.subscriber;

import hhplus.concertreservationservice.domain.concert.event.ConcertPaymentSuccessEvent;
import hhplus.concertreservationservice.domain.concert.event.producer.ConcertPaymentMessageProducer;
import hhplus.concertreservationservice.domain.concert.event.subscriber.PaymentEventSubscriber;
import hhplus.concertreservationservice.domain.outbox.dto.OutboxCommand;
import hhplus.concertreservationservice.domain.outbox.service.OutboxService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("[단위 테스트] PaymentEventSubscriberTest")
class PaymentEventSubscriberTest {

    @Mock
    private OutboxService outboxService;

    @Mock
    private ConcertPaymentMessageProducer concertPaymentMessageProducer;

    @InjectMocks
    private PaymentEventSubscriber paymentEventSubscriber;

    @Value("${spring.kafka.producer.topic.payment-success}")
    private String paymentSuccessTopic;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("[성공] ConcertPaymentSuccessEvent 발생 시 아웃박스 저장 및 메시지 전송 테스트")
    void testSendNotification() {
        // Given
        ConcertPaymentSuccessEvent event = ConcertPaymentSuccessEvent.builder()
            .userId(1L)
            .price(BigDecimal.valueOf(10000))
            .reservationId(1L)
            .paymentId(1L)
            .token("EXAMPLE-TOKEN")
            .build();

        String outboxId = "outboxTest";

        when(outboxService.saveOutboxAndGetId(any(OutboxCommand.Create.class))).thenReturn(outboxId);

        // When
        paymentEventSubscriber.sendNotification(event);

        // Then
        verify(outboxService).saveOutboxAndGetId(any(OutboxCommand.Create.class));
        verify(concertPaymentMessageProducer).sendSuccessMessage(event.paymentId().toString(), outboxId);
    }
}
