package hhplus.concertreservationservice.domain.concert.publisher;

import hhplus.concertreservationservice.domain.concert.dto.ConcertPaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConcertPaymentEventPublisher {
    private final ApplicationEventPublisher publisher;

    public void success(ConcertPaymentSuccessEvent event){
        publisher.publishEvent(event);
    }

}
