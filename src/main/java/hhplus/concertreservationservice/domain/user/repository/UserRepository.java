package hhplus.concertreservationservice.domain.user.repository;

import hhplus.concertreservationservice.domain.user.entity.User;
import java.util.Optional;

public interface UserRepository {

    void save(User user);

    Optional<User> findById(Long userId);
}
