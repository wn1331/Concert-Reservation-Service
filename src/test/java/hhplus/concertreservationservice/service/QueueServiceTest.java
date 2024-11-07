package hhplus.concertreservationservice.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hhplus.concertreservationservice.domain.concert.repository.ConcertReservationRepository;
import hhplus.concertreservationservice.domain.concert.repository.ConcertSeatRepository;

import hhplus.concertreservationservice.domain.queue.dto.QueueCommand;
import hhplus.concertreservationservice.domain.queue.dto.QueueInfo;
import hhplus.concertreservationservice.domain.queue.repository.QueueRepository;
import hhplus.concertreservationservice.domain.queue.service.QueueService;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import java.lang.reflect.Field;
import java.util.Set;
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
        // Given
        String queueToken = "validToken";
        QueueCommand.VerifyQueue command = new QueueCommand.VerifyQueue(queueToken);

        when(queueRepository.existActiveToken(queueToken)).thenReturn(true);

        // When & Then
        assertDoesNotThrow(() -> queueService.verifyQueue(command));
        verify(queueRepository, times(1)).existActiveToken(queueToken);
    }

    @Test
    @Order(2)
    @DisplayName("[실패] 대기열 검증(verifyQueue 메서드) - 아직 대기중")
    void testVerifyQueue_Failure_StillWaiting() {
        // Given
        String queueToken = "waitingToken";
        QueueCommand.VerifyQueue command = new QueueCommand.VerifyQueue(queueToken);

        when(queueRepository.existActiveToken(queueToken)).thenReturn(false);
        when(queueRepository.existWaitingToken(queueToken)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> queueService.verifyQueue(command))
            .isInstanceOf(CustomGlobalException.class)
            .hasMessage(ErrorCode.QUEUE_STILL_WAITING.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("[성공] 신규 대기열 생성 및 순번 반환")
    void testEnqueue_NewQueue() {
        // When
        QueueInfo.Enqueue result = queueService.enqueue();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.queueToken()).isNotEmpty();
        verify(queueRepository, times(1)).save(anyString(), anyLong());
    }


    @Test
    @Order(6)
    @DisplayName("[성공] 스케줄러 대기열 상태 변경(activateProcess 메서드) - waitToken에서 activeToken으로")
    void testActivateProcess() {
        // Given
        Set<String> waitingTokens = Set.of("token1", "token2");
        when(queueRepository.getWaitingTokens(0L, 30L)).thenReturn(waitingTokens);

        // When
        queueService.activateProcess();

        // Then
        verify(queueRepository, times(1)).getWaitingTokens(0L, 30L);
        verify(queueRepository, times(1)).deleteWaitingToken(waitingTokens);
        for (String token : waitingTokens) {
            verify(queueRepository, times(1)).addActiveToken(token);
        }
    }

    @Test
    @Order(8)
    @DisplayName("[성공] 대기열 토큰 만료 처리(expireToken 메서드)")
    void testExpireToken_Success() {
        // Given
        String queueToken = "activeToken";

        // When
        queueService.expireToken(queueToken);

        // Then
        verify(queueRepository, times(1)).deleteActiveToken(queueToken);
    }




}
