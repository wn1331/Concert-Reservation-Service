package hhplus.concertreservationservice.domain.outbox.dto;

import hhplus.concertreservationservice.domain.outbox.entity.Outbox;
import hhplus.concertreservationservice.domain.outbox.entity.Outbox.OutboxStatus;
import hhplus.concertreservationservice.domain.outbox.entity.Outbox.OutboxType;
import lombok.Builder;

public record OutboxCommand(
) {

    @Builder
    public record Create(
        String topic,
        String key,
        String payload,
        OutboxType type
    ){

        public Outbox toEntity() {
            return Outbox.builder()
                .topic(topic)
                .key(key)
                .type(type)
                .payload(payload)
                .status(OutboxStatus.INIT)
                .build();
        }
    }

}
