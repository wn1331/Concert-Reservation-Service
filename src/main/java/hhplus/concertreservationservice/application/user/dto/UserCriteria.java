package hhplus.concertreservationservice.application.user.dto;

import hhplus.concertreservationservice.domain.user.dto.UserCommand;
import hhplus.concertreservationservice.domain.user.entity.UserPointHistoryType;
import java.math.BigDecimal;
import lombok.Builder;

public record UserCriteria(

) {
    @Builder
    public record CheckBalance(
        Long userId

    ) {
        public UserCommand.CheckBalance toCommand(){
            return UserCommand.CheckBalance.builder()
                .userId(userId)
                .build();
        }

    }

    @Builder
    public record ChargeBalance(
        Long userId,
        BigDecimal amount
    ){

        public UserCommand.ChargeBalance toCommand(){
            return UserCommand.ChargeBalance.builder()
                .userId(userId)
                .amount(amount)
                .build();
        }

        public UserCommand.AddHistory toAddHistoryCommand(){
            return UserCommand.AddHistory.builder()
                .userId(userId)
                .type(UserPointHistoryType.CHARGE)
                .amount(amount)
                .build();
        }

    }



}
