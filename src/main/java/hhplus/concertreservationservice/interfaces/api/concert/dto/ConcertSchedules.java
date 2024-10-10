package hhplus.concertreservationservice.interfaces.api.concert.dto;

import java.time.LocalDate;
import java.util.List;

public record ConcertSchedules() {

    public record Response(
        List<ConcertScheduleResponse> schedules
    ) {
        public record ConcertScheduleResponse(
            Long scheduleId,
            LocalDate date,
            String status // 추후, Enum으로 변경 예정
        ){

        }

    }

}
