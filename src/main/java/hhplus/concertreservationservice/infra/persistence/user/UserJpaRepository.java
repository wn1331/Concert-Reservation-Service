package hhplus.concertreservationservice.infra.persistence.user;

import hhplus.concertreservationservice.domain.user.entity.User;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserJpaRepository extends JpaRepository<User,Long> {

    @Query("SELECT u FROM User u WHERE u.id = :id")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<User> findByIdForUsePoint(@Param(value = "id") Long userId);

    Optional<User> findUserById(Long userId);
}
