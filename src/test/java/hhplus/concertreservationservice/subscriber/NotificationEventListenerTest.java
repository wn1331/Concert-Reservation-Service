package hhplus.concertreservationservice.subscriber;
import hhplus.concertreservationservice.domain.common.notification.NotificationClient;
import hhplus.concertreservationservice.domain.concert.event.ConcertPaymentSuccessEvent;
import hhplus.concertreservationservice.domain.concert.event.subscriber.PaymentEventSubscriber;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

@DisplayName("[단위 테스트] NotificationEventListenerTest")
class NotificationEventListenerTest {

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private PaymentEventSubscriber eventSubscriber;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("이벤트 발생 시 NotificationClient가 호출되는지 테스트")
    void testSendNotification() {
        // Given
        ConcertPaymentSuccessEvent event = new ConcertPaymentSuccessEvent(1L, BigDecimal.valueOf(10000), 1L,1L);

        // When
        eventSubscriber.sendNotification(event); // 직접 메서드 호출

        // Then
        verify(notificationClient).sendPaymentNotification(event);
    }
}
