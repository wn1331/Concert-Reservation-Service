package hhplus.concertreservationservice.infra.persistence.user;

import hhplus.concertreservationservice.domain.user.entity.UserDetail;
import hhplus.concertreservationservice.domain.user.repository.UserDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserDetailRepositoryImpl implements UserDetailRepository {
    private final UserDetailJpaRepository jpaRepository;


    @Override
    public void save(UserDetail userDetail) {
        jpaRepository.save(userDetail);
    }
}
