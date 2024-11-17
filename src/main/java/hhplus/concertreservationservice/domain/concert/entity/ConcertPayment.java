package hhplus.concertreservationservice.domain.concert.entity;

import hhplus.concertreservationservice.domain.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "PAYMENT")
@EqualsAndHashCode(callSuper = false) // 객체 비교를 위해 필요함.(Equals, HashCode 재설정)
public class ConcertPayment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reservationId;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private PaymentStatusType status;

    @Builder
    public ConcertPayment(Long reservationId, BigDecimal price, PaymentStatusType status) {
        this.reservationId = reservationId;
        this.price = price;
        this.status = status;
    }
}
