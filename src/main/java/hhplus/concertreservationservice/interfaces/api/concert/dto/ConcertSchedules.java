package hhplus.concertreservationservice.interfaces.api.concert.dto;

import hhplus.concertreservationservice.domain.concert.entity.ScheduleStatusType;
import java.time.LocalDate;
import java.util.List;

public record ConcertSchedules() {

    public record Response(
        List<ConcertScheduleResponse> schedules
    ) {

        public record ConcertScheduleResponse(
            Long scheduleId,
            LocalDate date,
            ScheduleStatusType status
        ) {

        }

    }

}
