package hhplus.concertreservationservice.infra.persistence.queue;

import hhplus.concertreservationservice.domain.queue.repository.QueueRepository;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QueueRedisRepositoryImpl implements QueueRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String WAITING_QUEUE_KEY = "waitingQueue";
    private static final String ACTIVE_QUEUE_KEY = "activeQueue";

    private ZSetOperations<String, String> zSetOperations() {
        return redisTemplate.opsForZSet();
    }

    @Override
    public void save(String token, long nowMilliseconds) {
        zSetOperations().add(WAITING_QUEUE_KEY, token, nowMilliseconds);
    }

    @Override
    public Boolean existWaitingToken(String token) {
        Double score = zSetOperations().score(WAITING_QUEUE_KEY, token);
        return score != null;
    }

    @Override
    public Boolean existActiveToken(String token) {
        return redisTemplate.opsForValue().get(ACTIVE_QUEUE_KEY + ":" + token) != null;
    }

    @Override
    public void deleteActiveToken(String token) {
        String key = ACTIVE_QUEUE_KEY + ":" + token;
        redisTemplate.delete(key); // Strings 자료구조에서 key 삭제
    }

    @Override
    public Long order(String token) {
        Long rank = zSetOperations().rank(WAITING_QUEUE_KEY, token);
        return rank != null ? rank + 1 : 0;
    }

    @Override
    public Set<String> getWaitingTokens(Long start, Long end) {
        return zSetOperations().range(WAITING_QUEUE_KEY, start, end-1);
    }

    @Override
    public void deleteWaitingToken(Set<String> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            return; // tokens가 비어있는 경우 아무 작업도 수행하지 않음
        }
        zSetOperations().remove(WAITING_QUEUE_KEY, tokens.toArray(new String[0]));
    }

    @Override
    public void addActiveToken(String token) {
        // Strings 자료구조에 ActiveToken 저장 및 만료 시간 설정
        String key = ACTIVE_QUEUE_KEY + ":" + token;
        redisTemplate.opsForValue().set(key, token);
        redisTemplate.expire(key, 10, TimeUnit.MINUTES); // 10분 만료 시간 설정
        }
}
