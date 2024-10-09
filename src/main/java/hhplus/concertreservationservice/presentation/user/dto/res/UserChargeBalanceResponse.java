package hhplus.concertreservationservice.presentation.user.dto.res;

import java.math.BigDecimal;

public record UserChargeBalanceResponse(Long userId, BigDecimal amount) {

}
