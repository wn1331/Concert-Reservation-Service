package hhplus.concertreservationservice.domain.user.repository;

import hhplus.concertreservationservice.domain.user.entity.User;

public interface UserRepository {

    void save(User user);
}
