package hhplus.concertreservationservice.application.queue.dto;

import hhplus.concertreservationservice.domain.queue.dto.QueueCommand;
import lombok.Builder;

public record QueueCriteria() {

    @Builder
    public record Enqueue(
        Long userId
    ){

        public QueueCommand.Enqueue toCommand() {
            return QueueCommand.Enqueue.builder()
                .userId(userId)
                .build();
        }
    }

}
