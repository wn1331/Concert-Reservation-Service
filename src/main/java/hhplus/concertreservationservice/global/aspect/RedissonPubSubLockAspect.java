package hhplus.concertreservationservice.global.aspect;

import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import hhplus.concertreservationservice.global.utils.CustomSpringELParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RedissonPubSubLockAspect {

    private final RedissonClient redissonClient;
    private static final String TOPIC_NAME = "lockTopic";

    @Around("@annotation(redissionPubSubLock)")
    public Object handleRedissionPubSubLock(ProceedingJoinPoint joinPoint, RedissionPubSubLock redissionPubSubLock) throws Throwable {
        String lockKey = CustomSpringELParser.parseKey(joinPoint, redissionPubSubLock.value());
        if (lockKey == null) {
            throw new CustomGlobalException(ErrorCode.PUBSUB_LOCK_KEY_NOT_NULL);
        }

        RLock lock = redissonClient.getLock(lockKey);
        RTopic topic = redissonClient.getTopic(TOPIC_NAME);

        try {
            boolean isLocked = lock.tryLock(redissionPubSubLock.waitTime(), redissionPubSubLock.leaseTime(), redissionPubSubLock.timeUnit());
            if (!isLocked) {
                log.warn("Failed to acquire lock: {}", lockKey);
                throw new CustomGlobalException(ErrorCode.CANNOT_ACQUIRE_LOCK);
            }

            log.info("Lock acquired successfully: {}", lockKey);
            topic.publish("Lock acquired: " + lockKey);

            return joinPoint.proceed();
        } catch (Exception e) {
            throw new CustomGlobalException(ErrorCode.LOCK_INTERNAL_ERROR);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("Lock released: {}", lockKey);
                topic.publish("Lock released: " + lockKey);
            } else {
                log.warn("Lock not held by the current thread: {}", lockKey);
            }
        }
    }
}
