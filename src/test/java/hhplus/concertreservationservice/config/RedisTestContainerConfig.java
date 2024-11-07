package hhplus.concertreservationservice.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class RedisTestContainerConfig {
    private static final String REDIS_IMAGE = "redis:latest";
    private static final int REDIS_PORT = 1331;
    private static final GenericContainer REDIS_CONTAINER;

    static {
        REDIS_CONTAINER = new GenericContainer(REDIS_IMAGE)
            .withExposedPorts(REDIS_PORT)
            .withReuse(true);
        REDIS_CONTAINER.start();
    }

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(REDIS_PORT)
            .toString());
    }

}
