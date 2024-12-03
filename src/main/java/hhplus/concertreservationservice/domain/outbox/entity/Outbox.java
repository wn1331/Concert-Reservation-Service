package hhplus.concertreservationservice.domain.outbox.entity;

import hhplus.concertreservationservice.domain.BaseTimeEntity;
import jakarta.persistence.Column;
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
public class Outbox extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private String id;

    @Column(name = "`topic`")
    private String topic;

    @Column(name = "`key`")
    private String key;

    @Enumerated(EnumType.STRING)
    private OutboxType type;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    private String payload;


    public void processSuccess() {
        this.status = OutboxStatus.SUCCESS;
    }


    public enum OutboxType{
        RESERVATION,PAYMENT
    }


    public enum OutboxStatus {
        INIT,
        SUCCESS
    }

    @Builder
    public Outbox(OutboxType type, OutboxStatus status, String topic, String payload,
        String key) {
        this.type = type;
        this.status = status;
        this.topic = topic;
        this.payload = payload;
        this.key = key;
    }
}
