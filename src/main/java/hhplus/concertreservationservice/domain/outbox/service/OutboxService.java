package hhplus.concertreservationservice.domain.outbox.service;

import hhplus.concertreservationservice.domain.outbox.dto.OutboxCommand;
import hhplus.concertreservationservice.domain.outbox.entity.Outbox;
import hhplus.concertreservationservice.domain.outbox.repository.OutboxRepository;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;

    public Outbox findOutbox(String outboxId) {
        return outboxRepository.findByIdAndStatus(outboxId)
            .orElseThrow(() -> new CustomGlobalException(
                ErrorCode.OUTBOX_NOT_FOUND));

    }

    public String saveOutboxAndGetId(OutboxCommand.Create command) {
        // 아웃박스 생성 로직
        Outbox outbox = outboxRepository.save(command.toEntity());
        return outbox.getId();
    }

    public void updateOutbox(Outbox outbox) {
        // 아웃박스 상태를 변경하는 로직
        outbox.processSuccess();
        outboxRepository.save(outbox);
    }


    public List<Outbox> findFailedOutbox() {
        LocalDateTime validationType = LocalDateTime.now().minusMinutes(3);
        return outboxRepository.findFailedOutbox(validationType);

    }
}
