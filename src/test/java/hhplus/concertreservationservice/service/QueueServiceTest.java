package hhplus.concertreservationservice.service;


import hhplus.concertreservationservice.domain.concert.repository.ConcertReservationRepository;
import hhplus.concertreservationservice.domain.concert.repository.ConcertSeatRepository;

import hhplus.concertreservationservice.domain.queue.repository.QueueRepository;
import hhplus.concertreservationservice.domain.queue.service.QueueService;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.DisplayName;


@ExtendWith(MockitoExtension.class)
@DisplayName("[단위 테스트] QueueService")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QueueServiceTest {

    @Mock
    private QueueRepository queueRepository;

    @Mock
    private ConcertReservationRepository concertReservationRepository;

    @Mock
    private ConcertSeatRepository concertSeatRepository;



    @InjectMocks
    private QueueService queueService;





    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        // 리플렉션을 통해 QueueService의 private batchSize 필드를 설정
        Field batchSizeField = QueueService.class.getDeclaredField("batchSize");
        batchSizeField.setAccessible(true);  // private 필드 접근 가능하도록 설정
        batchSizeField.set(queueService, 30);  // batchSize 값을 설정

    }

    @Test
    @Order(1)
    @DisplayName("[성공] 대기열 검증(verifyQueue 메서드)")
    void testVerifyQueue_Success() {

    }

    @Test
    @Order(2)
    @DisplayName("[실패] 대기열 검증(verifyQueue 메서드) - 아직 대기중")
    void testVerifyQueue_Failure_StillWaiting() {

    }

    @Test
    @Order(3)
    @DisplayName("[성공] 대기열이 없을 때 (enqueueOrPoll 메서드) - 신규 대기열 생성 및 순번 반환")
    void testEnqueue_NewQueue() {
        // Given

    }


    @Test
    @Order(4)
    @DisplayName("[성공] 대기열이 존재할 때, WAITING상태일 때(enqueueOrPoll 메서드) - 토큰값과 순번 반환")
    void testEnqueue_ExistingQueue_WaitingStatus() {
        // Given

    }

    @Test
    @Order(5)
    @DisplayName("[성공] 대기열이 존재할 때, PASS 상태일 때(enqueueOrPoll 메서드) - 토큰값과 순번 반환")
    void testEnqueue_ExistingQueue_PassStatus() {
        // Given

    }

    @Test
    @Order(6)
    @DisplayName("[성공] 스케줄러 대기열 상태 변경(activateProcess 메서드) - WAITING에서 PASS로")
    void testActivateProcess() {


    }


    @Test
    @Order(7)
    @DisplayName("[성공] 스케줄러 만료된 대기열 삭제(expireProcess 메서드)")
    void testExpireProcess_Success() {

    }

    @Test
    @Order(8)
    @DisplayName("[성공] 대기열 토큰 만료 처리(expireToken 메서드)")
    void testExpireToken_Success() {

    }

    @Test
    @Order(9)
    @DisplayName("[실패] 대기열 토큰 만료 처리(expireToken 메서드) - 존재하지 않는 대기열 토큰")
    void testExpireToken_Failure_TokenNotFound() {

    }


    @Test
    @Order(10)
    @DisplayName("[성공] 대기열 검증 및 예약만료 체크(verifyQueueForPay 메서드) - 예약 시간이 5분 이내인 경우")
    void testVerifyQueueForPay_Success_Within5Minutes() {

    }

    @Test
    @Order(11)
    @DisplayName("[성공] 대기열 검증 및 예약만료 체크(verifyQueueForPay 메서드) - 예약 시간이 5분 초과인 경우")
    void testVerifyQueueForPay_Success_After5Minutes() {

    }

    @Test
    @Order(12)
    @DisplayName("[실패] 대기열 검증 및 예약만료 체크(verifyQueueForPay 메서드) - 예약이 존재하지 않을 때")
    void testVerifyQueueForPay_Failure_ReservationNotFound() {

    }

    @Test
    @Order(13)
    @DisplayName("[실패] 대기열 검증 및 예약만료 체크(verifyQueueForPay 메서드) - 예약만료 시 좌석이 존재하지 않을 때")
    void testVerifyQueueForPay_Failure_SeatNotFound() {

    }

    @Test
    @Order(8)
    @DisplayName("[성공] 대기열 토큰 만료 처리")
    void expireToken_Success() {

    }

    @Test
    @Order(9)
    @DisplayName("[실패] 대기열 토큰 만료 처리 - 토큰이 존재하지 않음")
    void expireToken_TokenNotFound() {

    }






}
