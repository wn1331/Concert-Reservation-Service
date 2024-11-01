package hhplus.concertreservationservice.global.listener;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonPubSubListener {

    private final RedissonClient redissonClient;
    private static final String TOPIC_NAME = "lockTopic";

    @PostConstruct
    public void subscribeToLockTopic() {
        redissonClient.getTopic(TOPIC_NAME).addListener(String.class,
            (channel, message) -> log.info("Received Pub/Sub message: {}", message)
        );
    }
}
