package hhplus.concertreservationservice.infra.persistence.user;

import hhplus.concertreservationservice.domain.user.entity.UserPointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPointHistoryJpaRepository extends JpaRepository<UserPointHistory,Long> {

    boolean existsByUserId(Long id);
}
