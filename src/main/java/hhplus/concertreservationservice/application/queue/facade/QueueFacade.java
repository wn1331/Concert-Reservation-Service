package hhplus.concertreservationservice.application.queue.facade;


import hhplus.concertreservationservice.application.queue.dto.QueueCriteria;
import hhplus.concertreservationservice.application.queue.dto.QueueCriteria.VerifyQueue;
import hhplus.concertreservationservice.application.queue.dto.QueueCriteria.VerifyQueueForPay;
import hhplus.concertreservationservice.application.queue.dto.QueueResult;
import hhplus.concertreservationservice.domain.queue.service.QueueService;
import hhplus.concertreservationservice.domain.user.service.UserService;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
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

    @Transactional
    public void queueValidation(VerifyQueue criteria){
        queueService.verifyQueue(criteria.toCommand());
    }


    public void queueValidationForPay(VerifyQueueForPay criteria) {
        // 대기열 검증 및 PASS된지 5분이 지났다면 제거 + 예약 만료 확인 (변경감지를 위한 트랜잭션 적용)
        if(queueService.verifyQueueForPay(criteria.toCommand())){
            throw new CustomGlobalException(ErrorCode.RESERVATION_TIMEOUT);
        }
    }

    public void expireToken(String queueToken){
        queueService.expireToken(queueToken);
    }
}
