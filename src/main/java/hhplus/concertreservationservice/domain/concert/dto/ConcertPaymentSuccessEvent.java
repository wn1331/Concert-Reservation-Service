package hhplus.concertreservationservice.domain.concert.dto;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record ConcertPaymentSuccessEvent(
    // 실제 운영에서는 유저이름, 콘서트명, 콘서트날짜, 가격이 필요할 듯함
    Long userId,
    BigDecimal price,
    Long reservationId,
    Long paymentId
) {

}
