package hhplus.concertreservationservice.domain.user.dto;

import hhplus.concertreservationservice.domain.user.entity.UserPointHistoryType;
import java.math.BigDecimal;
import lombok.Builder;

public record UserCommand() {

    @Builder
    public record CheckBalance(
        Long userId
    ) {

    }

    @Builder
    public record ChargeBalance(
        Long userId,
        BigDecimal amount
    ) {

    }

    @Builder
    public record AddHistory(
        Long userId,
        UserPointHistoryType type,
        BigDecimal amount
    ){

    }

    @Builder
    public record UserPay(
        Long userId,
        BigDecimal price
    ){

    }
}
