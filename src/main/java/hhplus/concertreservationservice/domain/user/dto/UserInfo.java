package hhplus.concertreservationservice.domain.user.dto;

import hhplus.concertreservationservice.domain.user.entity.User;
import java.math.BigDecimal;
import lombok.Builder;

public record UserInfo() {

    @Builder
    public record CheckBalance(
        Long userId,
        BigDecimal balance
    ){

        public UserInfo.CheckBalance fromEntity(User user){
            return CheckBalance.builder()
                .userId(user.getId())
                .balance(user.getPoint())
                .build();
        }
    }

    @Builder
    public record ChargeBalance(
        Long userId,
        BigDecimal balance
    ) {
        public static UserInfo.ChargeBalance fromEntity(User user){
            return ChargeBalance.builder()
                .userId(user.getId())
                .balance(user.getPoint())
                .build();
        }

    }
}
