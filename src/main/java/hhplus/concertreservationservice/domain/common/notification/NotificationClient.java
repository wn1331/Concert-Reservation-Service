package hhplus.concertreservationservice.domain.common.notification;

import hhplus.concertreservationservice.domain.concert.dto.ConcertPaymentSuccessEvent;

public interface NotificationClient {

    void sendPaymentNotification(ConcertPaymentSuccessEvent event);

}
