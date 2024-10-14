package hhplus.concertreservationservice.presentation.api.user.dto;

import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record UserRequest() {

    public record ChargeBalance(
        @PositiveOrZero BigDecimal amount
    ){

    }
}
