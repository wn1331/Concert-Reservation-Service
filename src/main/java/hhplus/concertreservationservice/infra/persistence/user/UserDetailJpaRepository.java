package hhplus.concertreservationservice.infra.persistence.user;

import hhplus.concertreservationservice.domain.user.entity.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailJpaRepository extends JpaRepository<UserDetail,Long> {

}
