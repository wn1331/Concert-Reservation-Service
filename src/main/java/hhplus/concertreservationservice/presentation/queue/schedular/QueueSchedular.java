package hhplus.concertreservationservice.presentation.queue.schedular;

import hhplus.concertreservationservice.application.queue.facade.QueueFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueSchedular {

    private final QueueFacade queueFacade;

    // 1분마다
    // 대기열 통과 스케줄러
    @Scheduled(cron = "0 */1 * * * *")
    public void activateProcess(){
        queueFacade.activateProcess();
    }

    // 1분마다
    // 대기열 만료 스케줄러
    @Scheduled(cron = "0 */1 * * * *")
    public void expiredProcess(){
        queueFacade.expireProcess();
    }


}
