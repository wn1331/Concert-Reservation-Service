package hhplus.concertreservationservice.domain.queue.service;

import hhplus.concertreservationservice.domain.concert.entity.ConcertReservation;
import hhplus.concertreservationservice.domain.concert.entity.ConcertSeat;
import hhplus.concertreservationservice.domain.concert.repository.ConcertReservationRepository;
import hhplus.concertreservationservice.domain.concert.repository.ConcertSeatRepository;
import hhplus.concertreservationservice.domain.queue.dto.QueueCommand;
import hhplus.concertreservationservice.domain.queue.dto.QueueCommand.VerifyQueueForPay;
import hhplus.concertreservationservice.domain.queue.dto.QueueInfo;
import hhplus.concertreservationservice.domain.queue.entity.Queue;
import hhplus.concertreservationservice.domain.queue.entity.QueueStatusType;
import hhplus.concertreservationservice.domain.queue.repository.QueueRepository;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueueService {

    @Value("${queue.batch-size}")
    private int batchSize;

    private final QueueRepository queueRepository;
    private final ConcertReservationRepository concertReservationRepository;
    private final ConcertSeatRepository concertSeatRepository;

    //대기열 검증 메서드
    public void verifyQueue(QueueCommand.VerifyQueue command){


    }

    // 대기열 생성
    public QueueInfo.Enqueue enqueue() {

        return null;
    }

    // 대기열 활성화
    public void activateProcess() {


        // 대기열 활성화 스케줄링 완료 로깅
        log.info("Complete scheduled activate queues. time : {}", LocalDateTime.now());
    }

    // 대기열 순번 조회
    public Object getQueueOrder(){
        return null;
    }

    // 결제 시 토큰 만료를 위한 메서드
    public void expireToken(String queueToken) {


    }
}

