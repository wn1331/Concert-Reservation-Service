package hhplus.concertreservationservice.infra.persistence.concert;

import hhplus.concertreservationservice.domain.concert.entity.ConcertSchedule;
import hhplus.concertreservationservice.domain.concert.repository.ConcertScheduleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConcertScheduleRepositoryImpl implements ConcertScheduleRepository {
    private final ConcertScheduleJpaRepository jpaRepository;


    @Override
    public void save(ConcertSchedule schedule) {
        jpaRepository.save(schedule);
    }

    @Override
    public void saveAll(List<ConcertSchedule> schedules) {
        jpaRepository.saveAll(schedules);
    }
}
