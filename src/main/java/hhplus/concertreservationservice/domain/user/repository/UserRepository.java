package hhplus.concertreservationservice.domain.user.repository;

import hhplus.concertreservationservice.domain.user.entity.User;
import java.util.Optional;

public interface UserRepository {

    void save(User user);

    Optional<User> findById(Long userId);

    Optional<User> findByIdForUsePoint(Long userId);

    Optional<User> findByIdForChargePoint(Long aLong);


    boolean existsById(Long userId);
}
