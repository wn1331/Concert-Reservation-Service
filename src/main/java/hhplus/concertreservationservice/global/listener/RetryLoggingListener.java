package hhplus.concertreservationservice.global.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;

@Slf4j
public class RetryLoggingListener implements RetryListener {

    @Override
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        // 재시도 시작 시 호출됨
        log.warn("Retry started...");

        return true; // true를 반환하여 재시도를 허용
    }


}
