package hhplus.concertreservationservice.infra.common;

import hhplus.concertreservationservice.domain.concert.event.ConcertPaymentSuccessEvent;
import hhplus.concertreservationservice.domain.common.notification.NotificationClient;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FCMNotificationClientImpl implements NotificationClient {

    @Override
    public void sendPaymentNotification(ConcertPaymentSuccessEvent event) {
        try {
            log.info("FCM으로 결제정보를 알림 전송 시작.");
            // 알림 보내는 코드 구현
        }catch (Exception e){
            throw new CustomGlobalException(ErrorCode.NOTIFICATION_ERROR);
        }

        log.info("{}번 유저, {}번 예약 {}원 결제 완료. {}",event.userId(),event.reservationId(),event.price(), LocalDateTime.now());

    }
}
