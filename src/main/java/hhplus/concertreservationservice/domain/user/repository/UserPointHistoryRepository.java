package hhplus.concertreservationservice.domain.user.repository;


import hhplus.concertreservationservice.domain.user.entity.UserPointHistory;

public interface UserPointHistoryRepository {

    void save(UserPointHistory userPointHistory);
}
