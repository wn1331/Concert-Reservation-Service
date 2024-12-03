package hhplus.concertreservationservice.presentation.user.dto;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public record UserRequest() {

    public record ChargeBalance(
        @DecimalMin(value = "0.0", inclusive = false, message = "양수인 실수를 입력해야 합니다.")
        BigDecimal amount
    ){

    }
}
