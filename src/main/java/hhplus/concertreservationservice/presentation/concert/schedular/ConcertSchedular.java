package hhplus.concertreservationservice.presentation.concert.schedular;


import hhplus.concertreservationservice.application.concert.facade.ConcertReservationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConcertSchedular {

    private final ConcertReservationFacade concertReservationFacade;

    // 1분마다
    // 예약 만료 스케줄러
    @Scheduled(cron = "0 */1 * * * *")
    public void expireReservationProcess(){
        concertReservationFacade.expireReservationProcess();

    }

}
