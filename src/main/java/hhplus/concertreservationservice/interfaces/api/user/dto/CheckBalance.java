package hhplus.concertreservationservice.interfaces.api.user.dto;

import java.math.BigDecimal;

public record CheckBalance() {

    public record Response(
        Long userId,
        BigDecimal amount
    ) {

    }
}
