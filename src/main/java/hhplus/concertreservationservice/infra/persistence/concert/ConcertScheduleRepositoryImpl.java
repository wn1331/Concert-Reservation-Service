package hhplus.concertreservationservice.infra.persistence.concert;

import hhplus.concertreservationservice.domain.concert.entity.ConcertSchedule;
import hhplus.concertreservationservice.domain.concert.repository.ConcertScheduleRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConcertScheduleRepositoryImpl implements ConcertScheduleRepository {
    private final ConcertScheduleJpaRepository jpaRepository;


    @Override
    public ConcertSchedule save(ConcertSchedule schedule) {
        return jpaRepository.save(schedule);
    }

    @Override
    public void saveAll(List<ConcertSchedule> schedules) {
        jpaRepository.saveAll(schedules);
    }

    @Override
    public List<ConcertSchedule> findByConcertIdAndConcertDateAfter(Long concertId) {
        return jpaRepository.findByConcertIdAndConcertDateAfter(concertId, LocalDate.now());
    }
}
