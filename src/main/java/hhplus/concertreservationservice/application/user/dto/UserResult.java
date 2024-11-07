package hhplus.concertreservationservice.application.user.dto;

import hhplus.concertreservationservice.domain.user.dto.UserInfo;
import java.math.BigDecimal;
import lombok.Builder;

public record UserResult(

) {

    @Builder
    public record CheckBalance(
        Long userId,
        BigDecimal balance
    ){

        public static CheckBalance fromInfo(UserInfo.CheckBalance userBalance) {
            return CheckBalance.builder()
                .userId(userBalance.userId())
                .balance(userBalance.balance())
                .build();
        }
    }

    @Builder
    public record ChargeBalance(
        Long userId,
        BigDecimal balance
    ){
        public static UserResult.ChargeBalance fromInfo(UserInfo.ChargeBalance info){
            return ChargeBalance.builder()
                .userId(info.userId())
                .balance(info.balance())
                .build();
        }
    }

}
