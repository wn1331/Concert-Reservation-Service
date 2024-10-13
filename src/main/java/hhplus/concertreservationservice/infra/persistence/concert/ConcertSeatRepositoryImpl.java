package hhplus.concertreservationservice.infra.persistence.concert;

import hhplus.concertreservationservice.domain.concert.entity.ConcertSeat;
import hhplus.concertreservationservice.domain.concert.repository.ConcertSeatRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConcertSeatRepositoryImpl implements ConcertSeatRepository {

    private final ConcertSeatJpaRepository jpaRepository;

    @Override
    public void save(ConcertSeat seat) {
        jpaRepository.save(seat);
    }

    @Override
    public void saveAll(List<ConcertSeat> seats) {
        jpaRepository.saveAll(seats);
    }
}
