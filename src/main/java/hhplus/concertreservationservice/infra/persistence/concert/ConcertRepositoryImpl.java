package hhplus.concertreservationservice.infra.persistence.concert;

import hhplus.concertreservationservice.domain.concert.entity.Concert;
import hhplus.concertreservationservice.domain.concert.repository.ConcertRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {

    private final ConcertJpaRepository jpaRepository;

    @Override
    public List<Concert> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Concert save(Concert concert) {
        return jpaRepository.save(concert);
    }

    @Override
    public Optional<Concert> findById(Long id) {
        return jpaRepository.findById(id);
    }
}
