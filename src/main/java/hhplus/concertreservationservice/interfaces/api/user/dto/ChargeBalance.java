package hhplus.concertreservationservice.interfaces.api.user.dto;

import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record ChargeBalance() {

    public record Request(
        @PositiveOrZero BigDecimal amount
    ) {

    }

    public record Response(
        Long userId, BigDecimal amount
    ) {

    }

}
