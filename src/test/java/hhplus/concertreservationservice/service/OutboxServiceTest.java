package hhplus.concertreservationservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import hhplus.concertreservationservice.domain.outbox.dto.OutboxCommand;
import hhplus.concertreservationservice.domain.outbox.entity.Outbox;
import hhplus.concertreservationservice.domain.outbox.entity.Outbox.OutboxStatus;
import hhplus.concertreservationservice.domain.outbox.repository.OutboxRepository;
import hhplus.concertreservationservice.domain.outbox.service.OutboxService;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("[단위테스트] OutboxService 단위테스트")
class OutboxServiceTest {

    @Mock
    private OutboxRepository outboxRepository;

    @InjectMocks
    private OutboxService outboxService;

    @Test
    @DisplayName("[성공] 성공적으로 Outbox를 조회")
    void findOutbox_Success() {
        // Given
        String outboxId = "12345";
        Outbox mockOutbox = Outbox.builder()
            .status(OutboxStatus.INIT)
            .build();

        when(outboxRepository.findByIdAndStatus(outboxId, OutboxStatus.INIT))
            .thenReturn(Optional.of(mockOutbox));

        // When
        Outbox result = outboxService.findOutbox(outboxId);

        // Then
        assertThat(result).isEqualTo(mockOutbox);
        verify(outboxRepository, times(1)).findByIdAndStatus(outboxId, OutboxStatus.INIT);
    }

    @Test
    @DisplayName("[실패] 존재하지 않는 Outbox 예외 발생")
    void findOutbox_NotFound() {
        // Given
        String outboxId = "12345";
        when(outboxRepository.findByIdAndStatus(outboxId, OutboxStatus.INIT))
            .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> outboxService.findOutbox(outboxId))
            .isInstanceOf(CustomGlobalException.class)
            .hasMessage(ErrorCode.OUTBOX_NOT_FOUND.getMessage());
        verify(outboxRepository, times(1)).findByIdAndStatus(outboxId, OutboxStatus.INIT);
    }

    @Test
    @DisplayName("[성공] Outbox 저장 성공")
    void saveOutboxAndGetId_Success() {
        // Given
        OutboxCommand.Create command = OutboxCommand.Create.builder()
            .topic("test-topic")
            .key("test-key")
            .payload("test-payload")
            .type(Outbox.OutboxType.PAYMENT)
            .build();

        Outbox mockOutbox = command.toEntity();
        when(outboxRepository.save(any(Outbox.class))).thenReturn(mockOutbox);

        // When
        String result = outboxService.saveOutboxAndGetId(command);

        // Then
        assertThat(result).isEqualTo(mockOutbox.getId());
        verify(outboxRepository, times(1)).save(any(Outbox.class));
    }

    @Test
    @DisplayName("[성공] Outbox 상태 업데이트 성공")
    void updateOutbox_Success() {
        // Given
        Outbox mockOutbox = Outbox.builder()
            .status(OutboxStatus.INIT)
            .build();

        // When
        outboxService.updateOutbox(mockOutbox);

        // Then
        assertThat(mockOutbox.getStatus()).isEqualTo(OutboxStatus.SUCCESS);
        verify(outboxRepository, times(1)).save(mockOutbox);
    }

    @Test
    @DisplayName("[성공] 실패한 Outbox 조회 성공")
    void findFailedOutbox_Success() {
        // Given
        Outbox failedOutbox = Outbox.builder()
            .status(OutboxStatus.INIT)
            .build();

        when(outboxRepository.findFailedOutbox(any(LocalDateTime.class)))
            .thenReturn(Collections.singletonList(failedOutbox));

        // When
        List<Outbox> result = outboxService.findFailedOutbox();

        // Then
        assertThat(result).hasSize(1).contains(failedOutbox);
        verify(outboxRepository, times(1)).findFailedOutbox(any(LocalDateTime.class));
    }

}
