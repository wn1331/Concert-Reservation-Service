package hhplus.concertreservationservice.service;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hhplus.concertreservationservice.domain.concert.entity.ConcertReservation;
import hhplus.concertreservationservice.domain.concert.entity.ConcertSeat;
import hhplus.concertreservationservice.domain.concert.entity.ReservationStatusType;
import hhplus.concertreservationservice.domain.concert.entity.SeatStatusType;
import hhplus.concertreservationservice.domain.concert.repository.ConcertReservationRepository;
import hhplus.concertreservationservice.domain.concert.repository.ConcertSeatRepository;
import hhplus.concertreservationservice.domain.queue.dto.QueueCommand;
import hhplus.concertreservationservice.domain.queue.dto.QueueCommand.VerifyQueue;
import hhplus.concertreservationservice.domain.queue.dto.QueueCommand.VerifyQueueForPay;
import hhplus.concertreservationservice.domain.queue.dto.QueueInfo;
import hhplus.concertreservationservice.domain.queue.entity.Queue;
import hhplus.concertreservationservice.domain.queue.entity.QueueStatusType;
import hhplus.concertreservationservice.domain.queue.repository.QueueRepository;
import hhplus.concertreservationservice.domain.queue.service.QueueService;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.DisplayName;
import org.springframework.data.domain.PageRequest;


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

    private Queue queue;
    private ConcertReservation reservation;
    private ConcertSeat concertSeat;

    private final String QUEUE_TOKEN = UUID.randomUUID().toString();
    private final Long USER_ID = 1L;
    private final Long RESERVATION_ID = 1L;



    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        // 리플렉션을 통해 QueueService의 private batchSize 필드를 설정
        Field batchSizeField = QueueService.class.getDeclaredField("batchSize");
        batchSizeField.setAccessible(true);  // private 필드 접근 가능하도록 설정
        batchSizeField.set(queueService, 30);  // batchSize 값을 설정

        queue = Queue.builder()
            .userId(USER_ID)
            .queueToken(QUEUE_TOKEN)
            .status(QueueStatusType.WAITING)
            .build();

        reservation = ConcertReservation.builder()
            .concertSeatId(1L)
            .status(ReservationStatusType.RESERVED)
            .build();

        concertSeat = ConcertSeat.builder()
            .status(SeatStatusType.EMPTY)
            .build();

        queue = spy(queue);
        reservation = spy(reservation);
        concertSeat = spy(concertSeat);
    }

    @Test
    @Order(1)
    @DisplayName("[성공] 대기열 검증(verifyQueue 메서드)")
    void testVerifyQueue_Success() {
        // Given
        queue.pass();
        when(queueRepository.findByQueueToken(QUEUE_TOKEN)).thenReturn(Optional.of(queue));


        // When
        queueService.verifyQueue(new VerifyQueue(QUEUE_TOKEN));

        // Then
        verify(queueRepository, times(1)).findByQueueToken(QUEUE_TOKEN);
    }

    @Test
    @Order(2)
    @DisplayName("[실패] 대기열 검증(verifyQueue 메서드) - 아직 대기중")
    void testVerifyQueue_Failure_StillWaiting() {
        // Given
        when(queueRepository.findByQueueToken(QUEUE_TOKEN)).thenReturn(Optional.of(queue));

        // When & Then
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> {
            queueService.verifyQueue(new VerifyQueue(QUEUE_TOKEN));
        });

        assertEquals(ErrorCode.QUEUE_STILL_WAITING, exception.getErrorCode());
        verify(queueRepository, times(1)).findByQueueToken(QUEUE_TOKEN);
    }

    @Test
    @Order(3)
    @DisplayName("[성공] 대기열이 없을 때 (enqueueOrPoll 메서드) - 신규 대기열 생성 및 순번 반환")
    void testEnqueueOrPoll_NewQueue() {
        // Given
        QueueCommand.Enqueue command = new QueueCommand.Enqueue(USER_ID);
        when(queueRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        // 새로 생성될 대기열 객체
        Queue newQueue = Queue.builder()
            .userId(USER_ID)
            .queueToken(QUEUE_TOKEN)
            .status(QueueStatusType.WAITING)
            .build();

        when(queueRepository.save(any(Queue.class))).thenReturn(newQueue);
        when(queueRepository.countWaitingUsersBefore(newQueue.getId())).thenReturn(0L);  // 새로 생성된 대기열이 처음이므로 0명

        // When
        QueueInfo.Enqueue result = queueService.enqueueOrPoll(command);

        // Then
        assertNotNull(result);
        assertEquals(QUEUE_TOKEN, result.queueToken());
        assertEquals(1L, result.order());  // 첫 번째 대기자
        verify(queueRepository, times(1)).findByUserId(USER_ID);
        verify(queueRepository, times(1)).save(any(Queue.class));
        verify(queueRepository, times(1)).countWaitingUsersBefore(newQueue.getId());
    }


    @Test
    @Order(4)
    @DisplayName("[성공] 대기열이 존재할 때, WAITING상태일 때(enqueueOrPoll 메서드) - 토큰값과 순번 반환")
    void testEnqueueOrPoll_ExistingQueue_WaitingStatus() {
        // Given
        QueueCommand.Enqueue command = new QueueCommand.Enqueue(USER_ID);
        when(queueRepository.findByUserId(USER_ID)).thenReturn(Optional.of(queue));
        when(queueRepository.countWaitingUsersBefore(queue.getId())).thenReturn(5L);  // 앞선 대기열이 5명 있다고 가정

        // When
        QueueInfo.Enqueue result = queueService.enqueueOrPoll(command);

        // Then
        assertNotNull(result);
        assertEquals(QUEUE_TOKEN, result.queueToken());
        assertEquals(6L, result.order());  // 앞선 대기열 5명 + 1 (본인)
        verify(queueRepository, times(1)).findByUserId(USER_ID);
        verify(queueRepository, times(1)).countWaitingUsersBefore(queue.getId());
    }

    @Test
    @Order(5)
    @DisplayName("[성공] 대기열이 존재할 때, PASS 상태일 때(enqueueOrPoll 메서드) - 토큰값과 순번 반환")
    void testEnqueueOrPoll_ExistingQueue_PassStatus() {
        // Given
        queue.pass();
        QueueCommand.Enqueue command = new QueueCommand.Enqueue(USER_ID);
        when(queueRepository.findByUserId(USER_ID)).thenReturn(Optional.of(queue));

        // When
        QueueInfo.Enqueue result = queueService.enqueueOrPoll(command);

        // Then
        assertNotNull(result);
        assertEquals(QUEUE_TOKEN, result.queueToken());
        assertEquals(0L, result.order());  // PASS 상태일 때는 대기열 순번 0
        verify(queueRepository, times(1)).findByUserId(USER_ID);
        verify(queueRepository, never()).countWaitingUsersBefore(anyLong());  // PASS일 때는 순번 계산하지 않음
    }

    @Test
    @Order(6)
    @DisplayName("[성공] 스케줄러 대기열 상태 변경(activateProcess 메서드) - WAITING에서 PASS로")
    void testActivateProcess() {
        // Given
        List<Queue> waitingQueues = List.of(queue);  // 대기열 목록 (WAITING 상태)

        when(queueRepository.findByStatusOrderByIdAsc(QueueStatusType.WAITING, PageRequest.of(0, 30)))
            .thenReturn(waitingQueues);

        // When
        queueService.activateProcess();

        // Then
        // 상태가 PASS로 변경되었는지 확인
        assertEquals(QueueStatusType.PASS, queue.getStatus());

        // queueRepository.findByStatusOrderByIdAsc() 메서드가 호출되었는지 확인
        verify(queueRepository, times(1))
            .findByStatusOrderByIdAsc(eq(QueueStatusType.WAITING), eq(PageRequest.of(0, 30)));

    }


    @Test
    @Order(7)
    @DisplayName("[성공] 스케줄러 만료된 대기열 삭제(expireProcess 메서드)")
    void testExpireProcess_Success() {
        // Given
        List<Queue> expiredQueues = List.of(queue);

        // Mock된 queueRepository에서 expiredQueues 반환하도록 설정
        when(queueRepository.findExpiredQueues(any(QueueStatusType.class), any(LocalDateTime.class)))
            .thenReturn(expiredQueues);

        // When
        queueService.expireProcess();

        // Then
        // ArgumentCaptor를 사용하여 findExpiredQueues의 호출 인자 캡처
        ArgumentCaptor<LocalDateTime> dateTimeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(queueRepository, times(1))
            .findExpiredQueues(eq(QueueStatusType.PASS), dateTimeCaptor.capture());

        // 캡처된 LocalDateTime 값을 이용해 검증
        LocalDateTime capturedTime = dateTimeCaptor.getValue();
        assertNotNull(capturedTime);

        // 만료된 대기열이 올바르게 삭제되었는지 확인
        verify(queueRepository, times(1)).deleteAllInBatch(expiredQueues);
    }

    @Test
    @Order(8)
    @DisplayName("[성공] 대기열 토큰 만료 처리(expireToken 메서드)")
    void testExpireToken_Success() {
        // Given
        when(queueRepository.findByQueueToken(QUEUE_TOKEN)).thenReturn(Optional.of(queue));

        // When
        queueService.expireToken(QUEUE_TOKEN);

        // Then
        // 해당 토큰으로 대기열 조회
        verify(queueRepository, times(1)).findByQueueToken(QUEUE_TOKEN);
        // 대기열 삭제 호출 확인
        verify(queueRepository, times(1)).delete(queue);
    }

    @Test
    @Order(9)
    @DisplayName("[실패] 대기열 토큰 만료 처리(expireToken 메서드) - 존재하지 않는 대기열 토큰")
    void testExpireToken_Failure_TokenNotFound() {
        // Given
        when(queueRepository.findByQueueToken(QUEUE_TOKEN)).thenReturn(Optional.empty());

        // When & Then
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> {
            queueService.expireToken(QUEUE_TOKEN);
        });

        // 에러 메시지 확인
        assertEquals(ErrorCode.QUEUE_NOT_FOUND, exception.getErrorCode());

        // 대기열 조회 시도가 있었는지 확인
        verify(queueRepository, times(1)).findByQueueToken(QUEUE_TOKEN);
        // 대기열 삭제는 호출되지 않음
        verify(queueRepository, never()).delete(any());
    }


    @Test
    @Order(10)
    @DisplayName("[성공] 대기열 검증 및 예약만료 체크(verifyQueueForPay 메서드) - 예약 시간이 5분 이내인 경우")
    void testVerifyQueueForPay_Success_Within5Minutes() {
        // Given
        queue.pass();
        VerifyQueueForPay command = new VerifyQueueForPay(QUEUE_TOKEN, RESERVATION_ID);
        when(queueRepository.findByQueueToken(QUEUE_TOKEN)).thenReturn(Optional.of(queue));
        when(concertReservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(reservation));
        when(reservation.getCreatedAt()).thenReturn(now());

        // When
        boolean result = queueService.verifyQueueForPay(command);

        // Then
        assertFalse(result);  // 5분 이내면 false 반환
        verify(queueRepository, never()).delete(any(Queue.class));  // 대기열 삭제되지 않음
        verify(concertSeatRepository, never()).findById(anyLong());  // 좌석 조회되지 않음
    }

    @Test
    @Order(11)
    @DisplayName("[성공] 대기열 검증 및 예약만료 체크(verifyQueueForPay 메서드) - 예약 시간이 5분 초과인 경우")
    void testVerifyQueueForPay_Success_After5Minutes() {
        // Given
        queue.pass();
        concertSeat.reserveSeat();
        when(reservation.getCreatedAt()).thenReturn(now().minusMinutes(6));// 예약 생성 시간이 5분을 초과
        VerifyQueueForPay command = new VerifyQueueForPay(QUEUE_TOKEN, RESERVATION_ID);

        when(queueRepository.findByQueueToken(QUEUE_TOKEN)).thenReturn(Optional.of(queue));
        when(concertReservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(reservation));
        when(concertSeatRepository.findById(1L)).thenReturn(Optional.of(concertSeat));

        // When
        boolean result = queueService.verifyQueueForPay(command);

        // Then
        assertTrue(result);  // 5분 초과면 true 반환
        // 예약 상태가 더티 체킹을 통해 변경된 것처럼 처리
        verify(queueRepository, times(1)).delete(queue);  // 대기열 삭제
        verify(concertSeatRepository, times(1)).findById(1L);  // 좌석 조회
        verify(concertSeat, times(1)).cancelSeatByReservation();  // 좌석 점유 해제
        verify(concertReservationRepository, times(1)).findById(RESERVATION_ID);

        // Mock에선 실제 DB 반영을 하지 않으므로 save() 호출을 하지 않고 더티체킹이 동작했다고 가정하자.
        verify(concertReservationRepository, never()).save(any(ConcertReservation.class));
    }

    @Test
    @Order(12)
    @DisplayName("[실패] 대기열 검증 및 예약만료 체크(verifyQueueForPay 메서드) - 예약이 존재하지 않을 때")
    void testVerifyQueueForPay_Failure_ReservationNotFound() {
        // Given
        queue.pass();
        VerifyQueueForPay command = new VerifyQueueForPay(QUEUE_TOKEN, RESERVATION_ID);
        when(queueRepository.findByQueueToken(QUEUE_TOKEN)).thenReturn(Optional.of(queue));
        when(concertReservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.empty());

        // When & Then
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> {
            queueService.verifyQueueForPay(command);
        });

        assertEquals(ErrorCode.CONCERT_RESERVATION_NOT_FOUND, exception.getErrorCode());
        verify(queueRepository, times(1)).findByQueueToken(QUEUE_TOKEN);
        verify(concertReservationRepository, times(1)).findById(RESERVATION_ID);
    }

    @Test
    @Order(13)
    @DisplayName("[실패] 대기열 검증 및 예약만료 체크(verifyQueueForPay 메서드) - 예약만료 시 좌석이 존재하지 않을 때")
    void testVerifyQueueForPay_Failure_SeatNotFound() {
        // Given
        queue.pass();
        when(reservation.getCreatedAt()).thenReturn(now().minusMinutes(6));// 예약 생성 시간이 5분을 초과
        VerifyQueueForPay command = new VerifyQueueForPay(QUEUE_TOKEN, RESERVATION_ID);

        when(queueRepository.findByQueueToken(QUEUE_TOKEN)).thenReturn(Optional.of(queue));
        when(concertReservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(reservation));
        when(concertSeatRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> {
            queueService.verifyQueueForPay(command);
        });

        assertEquals(ErrorCode.CONCERT_SEAT_NOT_FOUND, exception.getErrorCode());
        verify(queueRepository, times(1)).findByQueueToken(QUEUE_TOKEN);
        verify(concertReservationRepository, times(1)).findById(RESERVATION_ID);
        verify(concertSeatRepository, times(1)).findById(1L);
    }

    @Test
    @Order(8)
    @DisplayName("[성공] 대기열 토큰 만료 처리")
    void expireToken_Success() {
        // Given
        when(queueRepository.findByQueueToken("testQueueToken"))
            .thenReturn(Optional.of(queue));

        // When
        queueService.expireToken("testQueueToken");

        // Then
        verify(queueRepository, times(1)).findByQueueToken("testQueueToken");
        verify(queueRepository, times(1)).delete(queue);
    }

    @Test
    @Order(9)
    @DisplayName("[실패] 대기열 토큰 만료 처리 - 토큰이 존재하지 않음")
    void expireToken_TokenNotFound() {
        // Given
        when(queueRepository.findByQueueToken("invalidToken"))
            .thenReturn(Optional.empty());

        // When & Then
        CustomGlobalException exception = assertThrows(
            CustomGlobalException.class,
            () -> queueService.expireToken("invalidToken")
        );

        assertEquals(ErrorCode.QUEUE_NOT_FOUND, exception.getErrorCode());
        verify(queueRepository, times(1)).findByQueueToken("invalidToken");
        verify(queueRepository, times(0)).delete(any(Queue.class));
    }






}
