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
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "RESERVATION")
public class ConcertReservation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long concertSeatId;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private ReservationStatusType status;


    @Builder
    public ConcertReservation(Long userId, Long concertSeatId,BigDecimal price, ReservationStatusType status) {
        this.userId = userId;
        this.concertSeatId = concertSeatId;
        this.price = price;
        this.status = status;
    }

    public void confirmPayment() {
        if (this.status == ReservationStatusType.RESERVED) {
            this.status = ReservationStatusType.PAY_SUCCEED;
        } else {
            throw new CustomGlobalException(ErrorCode.ALREADY_PAID_OR_CANCELLED);
        }
    }

    public void cancelReservation(){
        if(this.status == ReservationStatusType.RESERVED){
            this.status = ReservationStatusType.CANCELED;
        } else {
            throw new CustomGlobalException(ErrorCode.RESERVATION_NOT_RESERVED);
        }
    }




}
