package hhplus.concertreservationservice.consumer;

import hhplus.concertreservationservice.domain.common.notification.NotificationClient;
import hhplus.concertreservationservice.domain.concert.event.ConcertPaymentSuccessEvent;
import hhplus.concertreservationservice.domain.concert.event.producer.ConcertPaymentMessageProducer;
import hhplus.concertreservationservice.domain.outbox.entity.Outbox;
import hhplus.concertreservationservice.domain.outbox.service.OutboxService;
import hhplus.concertreservationservice.domain.queue.service.QueueService;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import hhplus.concertreservationservice.presentation.concert.consumer.PaymentConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("[단위 테스트] PaymentConsumerTest")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PaymentConsumerTest {

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private OutboxService outboxService;

    @Mock
    private QueueService queueService;

    @InjectMocks
    private PaymentConsumer paymentConsumer;

    @Value("${spring.kafka.producer.topic.payment-success}")
    private String paymentSuccessTopic;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Order(0)
    @DisplayName("[성공] ConsumerRecord 수신 시 알림 전송 및 대기열 만료 처리 성공 테스트")
    void testSentPaymentNotificationSuccess() {
        // Given
        String outboxId = "outboxTest";
        ConsumerRecord<String, String> record = new ConsumerRecord<>("payment-success", 0, 0L, "key", outboxId);
        Outbox outbox = Outbox.builder()
            .type(Outbox.OutboxType.PAYMENT)
            .status(Outbox.OutboxStatus.INIT)
            .topic("payment-success")
            .payload("{\"userId\": 1, \"price\": 10000, \"reservationId\": 1, \"paymentId\": 1, \"token\": \"EXAMPLE-TOKEN\"}")
            .key("key")
            .build();
        ConcertPaymentSuccessEvent event = ConcertPaymentSuccessEvent.builder()
            .userId(1L)
            .price(BigDecimal.valueOf(10000))
            .reservationId(1L)
            .paymentId(1L)
            .token("EXAMPLE-TOKEN")
            .build();
        when(outboxService.findOutbox(outboxId)).thenReturn(outbox);

        // When
        paymentConsumer.sentPaymentNotification(record);

        // Then
        verify(notificationClient).sendPaymentNotification(event);
        verify(queueService).expireToken(event.token());
        verify(outboxService).updateOutbox(outbox);
    }

    @Test
    @Order(1)
    @DisplayName("[실패] Outbox 조회 실패 시 예외 발생 테스트")
    void testSentPaymentNotificationOutboxNotFound() {
        // Given
        String outboxId = "outboxTest";
        ConsumerRecord<String, String> record = new ConsumerRecord<>("payment-success", 0, 0L, "key", outboxId);
        when(outboxService.findOutbox(outboxId)).thenThrow(new CustomGlobalException(ErrorCode.OUTBOX_NOT_FOUND));

        // When. Then
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> paymentConsumer.sentPaymentNotification(record));
        assertEquals(ErrorCode.OUTBOX_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @Order(2)
    @DisplayName("[성공] 알림 전송 실패 시 예외 발생 테스트")
    void testSentPaymentNotificationFailureInNotification() {
        // Given
        String outboxId = "outboxTest";
        ConsumerRecord<String, String> record = new ConsumerRecord<>("payment-success", 0, 0L, "key", outboxId);
        Outbox outbox = Outbox.builder()
            .type(Outbox.OutboxType.PAYMENT)
            .status(Outbox.OutboxStatus.INIT)
            .topic("payment-success")
            .payload("{\"userId\": 1, \"price\": 10000, \"reservationId\": 1, \"paymentId\": 1, \"token\": \"EXAMPLE-TOKEN\"}")
            .key("key")
            .build();

        ConcertPaymentSuccessEvent event = ConcertPaymentSuccessEvent.builder()
            .userId(1L)
            .price(BigDecimal.valueOf(10000))
            .reservationId(1L)
            .paymentId(1L)
            .token("EXAMPLE-TOKEN")
            .build();

        when(outboxService.findOutbox(outboxId)).thenReturn(outbox);
        doThrow(new CustomGlobalException(ErrorCode.NOTIFICATION_ERROR)).when(notificationClient).sendPaymentNotification(event);

        // When, Then
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> paymentConsumer.sentPaymentNotification(record));
        assertEquals(ErrorCode.NOTIFICATION_ERROR, exception.getErrorCode());
        verify(outboxService, never()).updateOutbox(outbox);
    }
}
