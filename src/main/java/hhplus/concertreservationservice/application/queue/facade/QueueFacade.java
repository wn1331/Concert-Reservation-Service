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
    public QueueResult.Enqueue enqueue(QueueCriteria.Enqueue criteria) throws Exception {

        // 해당 유저아이디가 유효한지 확인
        userService.findUserValidation(criteria.toCommand());

        // 이미 대기열에 등록되어있는지 확인
        boolean isExistsInQueue = queueService.existsByUserID(criteria.toCommand());

        if (isExistsInQueue) {
            // 이미 대기열에 등록되어 있으면, 해당 대기열의 토큰을 바로 반환.(멱등성)
            return QueueResult.Enqueue.fromInfo(queueService.findByUserId(criteria.toCommand()));
        }


        // 대기열 등록, Result객체로 캡슐화 후 return
        return QueueResult.Enqueue.fromInfo(queueService.enqueue(criteria.toCommand()));
    }


    // 대기열 순번 조회



}
