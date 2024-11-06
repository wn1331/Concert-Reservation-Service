package hhplus.concertreservationservice.domain.queue.service;

import hhplus.concertreservationservice.domain.queue.dto.QueueCommand;
import hhplus.concertreservationservice.domain.queue.dto.QueueInfo;
import hhplus.concertreservationservice.domain.queue.dto.QueueInfo.Enqueue;
import hhplus.concertreservationservice.domain.queue.repository.QueueRepository;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueueService {

    @Value("${queue.batch-size}")
    private long batchSize;


    private final QueueRepository queueRepository;

    // 대기열 검증 메서드
    public void verifyQueue(QueueCommand.VerifyQueue command) {
        String queueToken = command.queueToken();
        // 액티브토큰에 있으면 early return
        if (queueRepository.existActiveToken(queueToken)) {
            return;
        }

        // waiting토큰에 있으면 Exception
        if (queueRepository.existWaitingToken(queueToken)) {
            throw new CustomGlobalException(ErrorCode.QUEUE_STILL_WAITING);
        }
        // 둘다 없으면 Exception
        throw new CustomGlobalException(ErrorCode.QUEUE_NOT_FOUND);
    }

    // 대기열 생성
    public QueueInfo.Enqueue enqueue() {
        return new Enqueue(queueRepository.save());
    }

    // 대기열 활성화
    public void activateProcess() {
        // 대기중 토큰 가져와서
        Set<String> waitingTokens = queueRepository.getWaitingTokens(1L, batchSize);
        // 제거하고
        queueRepository.deleteWaitingToken(waitingTokens);
        // 그 토큰들 액티브에 넣기
        for (String waitingToken : waitingTokens) {
            // 이 작업에는 만료처리까지 들어가야 한다
            queueRepository.addActiveToken(waitingToken);
        }
        // 대기열 활성화 스케줄링 완료 로깅
        log.info("Complete scheduled activate queues. time : {}", LocalDateTime.now());
    }

    // 대기열 순번 조회
    public QueueInfo.Order getQueueOrder(QueueCommand.Order command){
        return new QueueInfo.Order(queueRepository.order(command.token()));
    }

    // 결제 시 토큰 만료를 위한 메서드
    public void expireToken(String queueToken) {
        queueRepository.deleteActiveToken(queueToken);

    }
}

