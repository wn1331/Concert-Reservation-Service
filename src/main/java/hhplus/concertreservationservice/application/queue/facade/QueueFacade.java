package hhplus.concertreservationservice.application.queue.facade;


import hhplus.concertreservationservice.application.queue.dto.QueueCriteria;
import hhplus.concertreservationservice.application.queue.dto.QueueCriteria.VerifyQueue;
import hhplus.concertreservationservice.application.queue.dto.QueueResult;
import hhplus.concertreservationservice.domain.queue.service.QueueService;
import hhplus.concertreservationservice.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueFacade {

    private final QueueService queueService;
    private final UserService userService;

    // 대기열 토큰 발급
    public QueueResult.Enqueue enqueue(QueueCriteria.Enqueue criteria){

        // 해당 유저아이디가 유효한지 확인
        userService.existCheckUser(criteria.userId());

        // 대기열 등록
        return QueueResult.Enqueue.fromInfo(queueService.enqueue());

    }

    // 대기열 활성화 스케줄러 퍼사드
    public void activateProcess() {
        queueService.activateProcess();
    }

    // 대기열 검증 퍼사드
    public void queueValidation(VerifyQueue criteria){
        queueService.verifyQueue(criteria.toCommand());
    }

    // 토큰 폴링용
    public QueueResult.Order getQueueOrder(QueueCriteria.Order criteria) {
        return new QueueResult.Order(queueService.getQueueOrder(criteria.toCommand()).id());

    }
}
