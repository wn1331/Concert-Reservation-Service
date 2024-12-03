package hhplus.concertreservationservice.presentation.queue.dto;

import hhplus.concertreservationservice.application.queue.dto.QueueCriteria;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;


public record QueueRequest() {

    @Builder
    public record Enqueue(
        @NotNull(message = "유저 id를 입력해야 합니다.")
        Long userId
    ){

        public QueueCriteria.Enqueue toCriteria(){
            return QueueCriteria.Enqueue.builder()
                .userId(userId)
                .build();
        }

    }


}
