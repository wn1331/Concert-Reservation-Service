package hhplus.concertreservationservice.presentation.user.dto.res;

import java.math.BigDecimal;

public record UserCheckBalanceResponse(Long userId, BigDecimal amount) {

}

