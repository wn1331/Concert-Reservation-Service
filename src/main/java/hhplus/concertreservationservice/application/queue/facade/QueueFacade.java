package hhplus.concertreservationservice.application.queue.facade;


import hhplus.concertreservationservice.application.queue.dto.QueueCriteria;
import hhplus.concertreservationservice.application.queue.dto.QueueResult;
import hhplus.concertreservationservice.domain.queue.service.QueueService;
import hhplus.concertreservationservice.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class QueueFacade {

    private final QueueService queueService;
    private final UserService userService;
    // 대기열 토큰 발급
    @Transactional
    public QueueResult.Enqueue enqueue(QueueCriteria.Enqueue criteria){

        // 해당 유저아이디가 유효한지 확인
        userService.existCheckUser(criteria.userId());

        // 대기열 등록및 폴링 분기처리
        return QueueResult.Enqueue.fromInfo(queueService.enqueueOrPoll(criteria.toCommand()));

    }

    @Transactional
    public void activateProcess() {
        queueService.activateProcess();
    }

    @Transactional
    public void expireProcess() {
        queueService.expireProcess();
    }
}
