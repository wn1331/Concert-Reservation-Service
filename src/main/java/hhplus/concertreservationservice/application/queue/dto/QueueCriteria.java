package hhplus.concertreservationservice.application.queue.dto;

import hhplus.concertreservationservice.domain.queue.dto.QueueCommand;
import lombok.Builder;

public record QueueCriteria() {

    @Builder
    public record Enqueue(
        Long userId
    ) {

        public QueueCommand.Enqueue toCommand() {
            return QueueCommand.Enqueue.builder()
                .userId(userId)
                .build();
        }
    }

    @Builder
    public record VerifyQueue(
        String queueToken
    ) {

        public QueueCommand.VerifyQueue toCommand() {
            return QueueCommand.VerifyQueue.builder()
                .queueToken(queueToken)
                .build();
        }
    }

    @Builder
    public record VerifyQueueForPay(
        String queueToken,
        Long reservationId
    ) {

        public QueueCommand.VerifyQueueForPay toCommand() {
            return QueueCommand.VerifyQueueForPay.builder()
                .queueToken(queueToken)
                .reservationId(reservationId)
                .build();
        }
    }


}
