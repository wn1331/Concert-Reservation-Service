package hhplus.concertreservationservice.domain.queue.entity;

import hhplus.concertreservationservice.domain.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Queue extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String queueToken;

    @Enumerated(EnumType.STRING)
    private QueueStatusType status;


    @Builder
    public Queue(Long userId, String queueToken, QueueStatusType status) {
        this.userId = userId;
        this.queueToken = queueToken;
        this.status = status;
    }

    public void pass(){
        this.status = QueueStatusType.PASS;
    }

}
