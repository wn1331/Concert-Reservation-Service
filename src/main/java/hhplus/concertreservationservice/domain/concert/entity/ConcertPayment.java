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
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.objenesis.SpringObjenesis;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "PAYMENT")
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
