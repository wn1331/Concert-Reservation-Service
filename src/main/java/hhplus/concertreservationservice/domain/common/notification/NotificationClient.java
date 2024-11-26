package hhplus.concertreservationservice.domain.common.notification;

import hhplus.concertreservationservice.domain.concert.event.ConcertPaymentSuccessEvent;

public interface NotificationClient {

    void sendPaymentNotification(ConcertPaymentSuccessEvent event);

}
