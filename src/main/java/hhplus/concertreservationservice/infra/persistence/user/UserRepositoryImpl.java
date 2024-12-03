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

    @Override
    public Optional<User> findByIdForUsePoint(Long userId) {
        return jpaRepository.findByIdForUsePoint(userId);
    }

    @Override
    public boolean existsById(Long userId) {
        return jpaRepository.existsById(userId);
    }

    @Override
    public Optional<User> findByIdForChargePoint(Long userId) {
        return jpaRepository.findUserById(userId);
    }
}
