package hhplus.concertreservationservice.infra.persistence.user;

import hhplus.concertreservationservice.domain.user.entity.User;
import hhplus.concertreservationservice.domain.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository jpaRepository;

    @Override
    public void save(User user) {
        jpaRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return jpaRepository.findById(userId);
    }
}
