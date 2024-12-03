package hhplus.concertreservationservice.presentation.concert.dto;

import hhplus.concertreservationservice.application.concert.dto.ConcertCriteria;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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

        public ConcertCriteria.Pay toCriteria(Long reservationId,String token) {
            return ConcertCriteria.Pay.builder()
                .reservationId(reservationId)
                .userId(userId)
                .token(token)
                .build();
        }
    }

    public record Create(
        @NotBlank(message = "콘서트 타이틀은 빈값이거나 null일 수 없습니다.")
        String title,
        List<LocalDate> dates,
        @NotNull(message = "콘서트 좌석 수는 null일 수 없습니다.")
        Integer seatAmount,
        @DecimalMin(value = "0.0", inclusive = false, message = "양수인 실수를 입력해야 합니다.")
        BigDecimal price

    ) {
        public ConcertCriteria.Create toCriteria() {
            if(dates.isEmpty()){
                throw new CustomGlobalException(ErrorCode.CONCERT_SCHEDULE_IS_EMPTY);
            }
            return ConcertCriteria.Create.builder()
                .title(title)
                .dates(dates)
                .seatAmount(seatAmount)
                .price(price)
                .build();
        }

    }
}
