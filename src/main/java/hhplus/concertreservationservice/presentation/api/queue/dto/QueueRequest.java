package hhplus.concertreservationservice.presentation.api.queue.dto;

import hhplus.concertreservationservice.application.queue.dto.QueueCriteria;
import lombok.Builder;


public record QueueRequest() {

    @Builder
    public record Enqueue(
        Long userId
    ){

        public QueueCriteria.Enqueue toCriteria(){
            return QueueCriteria.Enqueue.builder()
                .userId(userId)
                .build();
        }

    }


}
