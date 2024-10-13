package hhplus.concertreservationservice.domain.concert.entity;

import hhplus.concertreservationservice.domain.entity.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ConcertSeat extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long concertScheduleId;

    private String seatNum;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private SeatStatusType status;

    @Builder
    public ConcertSeat(Long concertScheduleId, String seatNum, BigDecimal price,
        SeatStatusType status) {
        this.concertScheduleId = concertScheduleId;
        this.seatNum = seatNum;
        this.price = price;
        this.status = status;
    }


}
