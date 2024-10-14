package hhplus.concertreservationservice.application.queue.dto;

import hhplus.concertreservationservice.domain.queue.dto.QueueInfo;
import lombok.Builder;

public record QueueResult() {

    @Builder
    public record Enqueue(
        String token
    ){
        public static QueueResult.Enqueue fromInfo(QueueInfo.Enqueue info){
            return Enqueue.builder()
                .token(info.token())
                .build();
        }
    }

}
