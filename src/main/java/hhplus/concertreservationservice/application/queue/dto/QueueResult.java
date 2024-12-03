package hhplus.concertreservationservice.application.queue.dto;

import hhplus.concertreservationservice.domain.queue.dto.QueueInfo;
import lombok.Builder;

public record QueueResult() {

    @Builder
    public record Enqueue(
        String queueToken
    ){
        public static QueueResult.Enqueue fromInfo(QueueInfo.Enqueue info){
            return Enqueue.builder()
                .queueToken(info.queueToken())
                .build();
        }
    }

    @Builder
    public record Order(
        Long order
    ) {

    }
}
