package hhplus.concertreservationservice.domain.concert.subscriber;

import hhplus.concertreservationservice.domain.common.notification.NotificationClient;
import hhplus.concertreservationservice.domain.concert.dto.ConcertPaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
public class NotificationEventSubscriber {
    private final NotificationClient notificationClient;

    // 커밋이 완료된 시점에 수행
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendNotification(ConcertPaymentSuccessEvent event) {
        notificationClient.sendPaymentNotification(event);
    }


}
