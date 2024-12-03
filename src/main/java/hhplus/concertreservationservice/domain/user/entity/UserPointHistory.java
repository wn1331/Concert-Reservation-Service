package hhplus.concertreservationservice.domain.user.entity;

import hhplus.concertreservationservice.domain.BaseCreatedTimeEntity;
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
public class UserPointHistory extends BaseCreatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private UserPointHistoryType type;

    private BigDecimal requestPoint;


    @Builder
    public UserPointHistory(Long userId, UserPointHistoryType type, BigDecimal requestPoint) {
        this.userId = userId;
        this.type = type;
        this.requestPoint = requestPoint;
    }
}
