package hhplus.concertreservationservice.presentation.concert.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import hhplus.concertreservationservice.application.concert.dto.ConcertCriteria;
import hhplus.concertreservationservice.application.concert.dto.ConcertCriteria.Pay;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ConcertRequest() {

    public record ReserveSeat(
        @NotNull(message = "유저 id를 입력해야 합니다.")
        Long userId,
        @NotNull(message = "좌석 id를 입력해야 합니다.")
        Long concertSeatId
    ) {

        public ConcertCriteria.ReserveSeat toCriteria() {
            return ConcertCriteria.ReserveSeat.builder()
                .userId(userId)
                .concertSeatId(concertSeatId)
                .build();
        }

    }

    public record Pay(
        @NotNull(message = "유저 id를 입력해야 합니다.")
        Long userId
    ) {

        public ConcertCriteria.Pay toCriteria(Long reservationId) {
            return ConcertCriteria.Pay.builder()
                .reservationId(reservationId)
                .userId(userId)
                .build();
        }
    }

}
