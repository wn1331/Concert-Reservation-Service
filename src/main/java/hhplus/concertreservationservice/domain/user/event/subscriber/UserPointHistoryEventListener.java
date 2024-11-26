package hhplus.concertreservationservice.domain.user.event.subscriber;

import hhplus.concertreservationservice.domain.concert.event.ConcertPaymentSuccessEvent;
import hhplus.concertreservationservice.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserPointHistoryEventListener {

    private final UserService userService;

    // 분산 트랜잭션을 사용하지 않는 환경에서 개발 측면에서 특정 로직 관심사 분리. AFTER_COMMIT 또는 분산 트랜잭션이라면 보상 트랜잭션을 구현해야 함.
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saveUserPaymentHistory(ConcertPaymentSuccessEvent event) {
        log.info("유저 결제이력 이벤트 {}를 수행합니다.", event);
        userService.saveUserPaymentHistory(event);
    }

}
