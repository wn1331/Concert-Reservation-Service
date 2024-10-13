package hhplus.concertreservationservice.domain.concert.repository;

import hhplus.concertreservationservice.domain.concert.entity.ConcertSchedule;
import java.util.List;

public interface ConcertScheduleRepository {

    void save(ConcertSchedule schedule);

    void saveAll(List<ConcertSchedule> schedules);
}
