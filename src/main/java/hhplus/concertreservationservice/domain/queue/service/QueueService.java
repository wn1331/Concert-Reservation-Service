package hhplus.concertreservationservice.domain.queue.service;

import hhplus.concertreservationservice.domain.concert.entity.ConcertReservation;
import hhplus.concertreservationservice.domain.concert.entity.ConcertSeat;
import hhplus.concertreservationservice.domain.concert.repository.ConcertReservationRepository;
import hhplus.concertreservationservice.domain.concert.repository.ConcertSeatRepository;
import hhplus.concertreservationservice.domain.queue.dto.QueueCommand;
import hhplus.concertreservationservice.domain.queue.dto.QueueCommand.Enqueue;
import hhplus.concertreservationservice.domain.queue.dto.QueueCommand.VerifyQueueForPay;
import hhplus.concertreservationservice.domain.queue.dto.QueueInfo;
import hhplus.concertreservationservice.domain.queue.entity.Queue;
import hhplus.concertreservationservice.domain.queue.entity.QueueStatusType;
import hhplus.concertreservationservice.domain.queue.repository.QueueRepository;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
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

    public void verifyQueue(QueueCommand.VerifyQueue command){
        // 요청받은 토큰의 대기열이 존재하지 않으면
        Queue queue = queueRepository.findByQueueToken(command.queueToken())
            .orElseThrow(() -> new CustomGlobalException(ErrorCode.QUEUE_NOT_FOUND));

        // 아직 대기열에 WAITING으로 있다면 Exception
        if (queue.getStatus() == QueueStatusType.WAITING) {
            throw new CustomGlobalException(ErrorCode.QUEUE_STILL_WAITING);
        }

    }

    public void enqueueOrPoll(Enqueue command) {
        queueRepository.save(Queue.builder()
            .userId(command.userId())
            .queueToken(UUID.randomUUID().toString())
            .status(QueueStatusType.WAITING)
            .build());
    }

    public void activateProcess() {
        List<Queue> waitingQueues = queueRepository.findByStatusOrderByIdAsc(
            QueueStatusType.WAITING, PageRequest.of(0, batchSize));

        log.info("Passed queue size : {}, time : {}", waitingQueues.size(), LocalDateTime.now());


        // 상태를 WAITING에서 PASS로 변경
        waitingQueues.forEach(Queue::pass);

        // 스케줄링 완료 로깅
        log.info("Complete scheduled activate queues. time : {}", LocalDateTime.now());
    }

    public void expireProcess() {
        //
        List<Queue> expiredQueues = queueRepository.findExpiredQueues(QueueStatusType.PASS,
            LocalDateTime.now().minusMinutes(5));

        log.info("expired queue size : {}, time : {}", expiredQueues.size(), LocalDateTime.now());

        // jpa Batch Delete 사용하기!
        queueRepository.deleteAllInBatch(expiredQueues);

        // 스케줄링 완료 로깅
        log.info("Complete scheduled expire queues. time : {}", LocalDateTime.now());
    }

    @Transactional
    public boolean verifyQueueForPay(VerifyQueueForPay command) {
        // 요청받은 토큰의 대기열 확인
        Queue queue = queueRepository.findByQueueToken(command.queueToken())
            .orElseThrow(() -> new CustomGlobalException(ErrorCode.QUEUE_NOT_FOUND));

        // 아직 대기열에 WAITING으로 있다면 Exception
        if (queue.getStatus() == QueueStatusType.WAITING) {
            throw new CustomGlobalException(ErrorCode.QUEUE_STILL_WAITING);
        }

        // 예약 조회
        ConcertReservation reservation = concertReservationRepository.findById(
                command.reservationId())
            .orElseThrow(() -> new CustomGlobalException(ErrorCode.CONCERT_RESERVATION_NOT_FOUND));

        // 예약정보가 5분 지났으면
        if(LocalDateTime.now().isAfter(reservation.getCreatedAt().plusMinutes(5))) {
            // 대기열 삭제
            queueRepository.delete(queue);

            //좌석점유 해제 (좌석부터 조회)
            ConcertSeat concertSeat = concertSeatRepository.findById(reservation.getConcertSeatId())
                .orElseThrow(() -> new CustomGlobalException(ErrorCode.CONCERT_SEAT_NOT_FOUND));
            concertSeat.cancelSeatByReservation();

            // 예약정보 update 더티체킹 사용
            reservation.cancelReservation();

            // true를 리턴하면 퍼사드에서 Exception 발생
            return true;
        }
        // false를 리턴하면 대기열 + 예약 검증에 통과.
        return false;

    }

    @Transactional
    public void expireToken(String queueToken) {

        // 토큰 조회
        Queue queue = queueRepository.findByQueueToken(queueToken)
            .orElseThrow(() -> new CustomGlobalException(ErrorCode.QUEUE_NOT_FOUND));

        // 토큰 삭제
        queueRepository.delete(queue);

    }
}

