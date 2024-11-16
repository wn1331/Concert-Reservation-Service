package hhplus.concertreservationservice.infra.common;

import hhplus.concertreservationservice.domain.concert.dto.ConcertPaymentSuccessEvent;
import hhplus.concertreservationservice.domain.common.notification.NotificationClient;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FCMNotificationClientImpl implements NotificationClient {

    @Override
    public void sendPaymentNotification(ConcertPaymentSuccessEvent event) {
        log.info("FCM으로 결제정보를 알림 전송 시작.");
        log.info("{}번 유저, {}번 예약 {}원 결제 완료. {}",event.userId(),event.reservationId(),event.price(), LocalDateTime.now());

        // 알림 보내는 코드 구현
    }
}
