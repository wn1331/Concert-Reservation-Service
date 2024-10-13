package hhplus.concertreservationservice.infra.persistence.user;

import hhplus.concertreservationservice.domain.user.repository.UserPointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserPointHistoryRepositoryImpl implements UserPointHistoryRepository {
    private final UserPointHistoryJpaRepository jpaRepository;


}
