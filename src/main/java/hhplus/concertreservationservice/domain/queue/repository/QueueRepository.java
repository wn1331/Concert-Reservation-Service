package hhplus.concertreservationservice.domain.queue.repository;

import java.util.Set;

public interface QueueRepository {

    void save(String token, long nowMilliseconds);
    Boolean existWaitingToken(String token);
    Boolean existActiveToken(String token);
    void deleteActiveToken(String token);
    Long order(String token);
    Set<String> getWaitingTokens(Long start,Long end);
    void deleteWaitingToken(Set<String> tokens);
    void addActiveToken(String token);

}
