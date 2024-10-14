package hhplus.concertreservationservice.presentation.api.user.dto;

import java.math.BigDecimal;

public record UserResponse() {

    public record ChargeBalance(
        Long userId,
        BigDecimal amount
    ){
    }

    public record CheckBalance(
        Long userId,
        BigDecimal amount
    ){
    }

}
