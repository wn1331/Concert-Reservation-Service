package hhplus.concertreservationservice.domain.user.repository;

import hhplus.concertreservationservice.domain.user.entity.User;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;

public interface UserRepository {

    void save(User user);

    Optional<User> findById(Long userId);

    Optional<User> findByIdForUsePoint(Long userId);


    boolean existsById(Long userId);
}
