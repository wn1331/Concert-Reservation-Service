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
public class RedissonDistributedLockAspect {

    private final RedissonClient redissonClient;
    private static final String TOPIC_NAME = "lockTopic";  // 락 상태를 전달하는 Pub/Sub 토픽 이름

    @Around("@annotation(distributedLock)")
    public Object handleRedissionPubSubLock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        // 락 키 생성 및 검증
        String lockKey = CustomSpringELParser.parseKey(joinPoint, distributedLock.value());
        if (lockKey == null) {
            throw new CustomGlobalException(ErrorCode.PUBSUB_LOCK_KEY_NOT_NULL);  // 락 키가 없을 경우 예외 발생
        }

        RLock lock = redissonClient.getLock(lockKey);  // 특정 리소스에 대한 고유 락 생성
        RTopic topic = redissonClient.getTopic(TOPIC_NAME);  // 락 상태 전달용 Pub/Sub 토픽 설정

        try {
            // 락 획득 시도: 지정된 시간 동안 락을 시도하고, 실패 시 경고 및 예외 처리
            boolean isLocked = lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
            if (!isLocked) {
                log.warn("Failed to acquire lock: {}", lockKey);
                throw new CustomGlobalException(ErrorCode.CANNOT_ACQUIRE_LOCK);
            }

            // 락 획득 성공 시, 다른 인스턴스에 락 상태 알림 발행
            log.info("Lock acquired successfully: {}", lockKey);
            topic.publish("Lock acquired: " + lockKey);

            // 비즈니스 로직 수행
            return joinPoint.proceed();
        }catch (CustomGlobalException e) {
            // CustomGlobalException을 그대로 던짐
            log.info("Error Occurred CustomGlobalException!!:{} ", lockKey);
            throw e;
        } catch (Exception e) {
            // 그 외의 예외는 LOCK_INTERNAL_ERROR로 처리
            log.info("Lock Undefined Error!!: {}", lockKey);
            throw new CustomGlobalException(ErrorCode.LOCK_INTERNAL_ERROR);
        }finally {
            // 락 해제 및 해제 알림 발행
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("Lock released: {}", lockKey);
                topic.publish("Lock released: " + lockKey);  // 락 해제 후, 대기 중인 인스턴스에 알림 발행
            } else {
                log.warn("Lock not held by the current thread: {}", lockKey);  // 현재 쓰레드가 락을 점유하지 않은 경우 경고 로그
            }
        }
    }
}
