package hhplus.concertreservationservice.interfaces.api.concert.dto;

import jakarta.validation.constraints.NotBlank;

public record ConcertPay() {

    public record Request(
        Long userId
    ) {

    }

    public record Response(
        Long reservationId,
        String paymentStatus // 추후, Enum으로 변경
    ) {

    }

}
