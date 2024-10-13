package hhplus.concertreservationservice.interfaces.api.concert.dto;

import hhplus.concertreservationservice.domain.concert.entity.SeatStatusType;
import java.util.List;

public record ConcertSeats() {

    public record Response(

        List<ConcertSeatResponse> concertSeats

    ) {
        public record ConcertSeatResponse(
            Long seatId,
            Integer seatNo,
            SeatStatusType status
        ){

        }

    }
}
