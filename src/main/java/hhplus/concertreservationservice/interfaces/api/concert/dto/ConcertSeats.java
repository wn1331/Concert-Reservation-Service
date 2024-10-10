package hhplus.concertreservationservice.interfaces.api.concert.dto;

import java.util.List;

public record ConcertSeats() {

    public record Response(

        List<ConcertSeatResponse> concertSeats

    ) {
        public record ConcertSeatResponse(
            Long seatId,
            Integer seatNo,
            String status // 추후 Enum으로 변경 예정
        ){

        }

    }
}
