package hhplus.concertreservationservice.global.aspect;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedissionPubSubLock {
    String value(); // Lock의 이름 (고유값)
    long waitTime() default 5L; // Lock획득을 시도하는 최대 시간 (ms)
    long leaseTime() default 2L; // 락을 획득한 후, 점유하는 최대 시간 (ms)
    TimeUnit timeUnit() default TimeUnit.SECONDS; // 락 시간 단위


}
