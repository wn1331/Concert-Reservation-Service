package hhplus.concertreservationservice.interfaces.api.concert.dto;

import hhplus.concertreservationservice.domain.concert.entity.PaymentStatusType;
import jakarta.validation.constraints.NotNull;

public record ConcertPay() {

    public record Request(
        @NotNull
        Long userId
    ) {

    }

    public record Response(
        Long reservationId,
        PaymentStatusType status // 추후, Enum으로 변경
    ) {

    }

}
