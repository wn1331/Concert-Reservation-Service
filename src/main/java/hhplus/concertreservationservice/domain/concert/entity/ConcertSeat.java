package hhplus.concertreservationservice.domain.concert.entity;

import hhplus.concertreservationservice.domain.BaseTimeEntity;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
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

    public void reserveSeat() {
        switch (this.status) {
            case EMPTY -> this.status = SeatStatusType.RESERVED;
            case RESERVED -> throw new CustomGlobalException(ErrorCode.ALREADY_RESERVED_SEAT);
            case SOLD -> throw new CustomGlobalException(ErrorCode.ALREADY_SOLD_SEAT);
        }
    }

    public void cancelSeatByReservation() {
        if (this.status == SeatStatusType.RESERVED) {
            this.status = SeatStatusType.EMPTY;
        } else {
            throw new CustomGlobalException(ErrorCode.SEAT_NOT_RESERVED);
        }
    }

    public void confirmSeatByPayment() {
        if (this.status == SeatStatusType.RESERVED) {
            this.status = SeatStatusType.SOLD;
        } else {
            throw new CustomGlobalException(ErrorCode.SEAT_NOT_RESERVED);
        }
    }




}
