package hhplus.concertreservationservice.presentation.concert.schedular;


import hhplus.concertreservationservice.application.concert.facade.ConcertPaymentFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentMessageSchedular {

    private final ConcertPaymentFacade concertPaymentFacade;

    @Scheduled(fixedDelay = 3 * 60 * 1000)
    public void retryMessage(){
        concertPaymentFacade.retryMessage();
    }

}
