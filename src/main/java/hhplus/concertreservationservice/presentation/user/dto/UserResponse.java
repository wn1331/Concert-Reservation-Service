package hhplus.concertreservationservice.presentation.user.dto;

import hhplus.concertreservationservice.application.user.dto.UserResult;
import hhplus.concertreservationservice.application.user.dto.UserResult.ChargeBalance;
import hhplus.concertreservationservice.application.user.dto.UserResult.CheckBalance;
import java.math.BigDecimal;
import lombok.Builder;

public record UserResponse() {

    @Builder
    public record CheckBalance(
        Long userId,
        BigDecimal balance
    ){

        public static CheckBalance fromResult(UserResult.CheckBalance checkBalance) {
            return CheckBalance.builder()
                .userId(checkBalance.userId())
                .balance(checkBalance.balance())
                .build();
        }
    }

    @Builder
    public record ChargeBalance(
        Long userId,
        BigDecimal amount
    ){

        public static ChargeBalance fromResult(UserResult.ChargeBalance chargeBalance) {
            return ChargeBalance.builder()
                .userId(chargeBalance.userId())
                .amount(chargeBalance.balance())
               .build();
        }
    }


}
