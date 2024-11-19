package hhplus.concertreservationservice.domain.outbox.entity;

import hhplus.concertreservationservice.domain.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Outbox extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    private String id;

    private Long targetId;

    private OutboxType type;

    private OutboxStatus status;

    private String topic;

    private String payload;

    private String key;


    public enum OutboxType{
        RESERVATION,PAYMENT
    }


    public enum OutboxStatus {
        INIT,
        RECEIVED,
        SUCCESS
    }

}
