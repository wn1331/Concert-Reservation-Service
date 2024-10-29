package hhplus.concertreservationservice.application.user.facade;

import hhplus.concertreservationservice.application.user.dto.UserCriteria.ChargeBalance;
import hhplus.concertreservationservice.application.user.dto.UserCriteria.CheckBalance;
import hhplus.concertreservationservice.application.user.dto.UserResult;
import hhplus.concertreservationservice.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;

    // 잔액조회
    @Transactional(readOnly = true)
    public UserResult.CheckBalance checkBalance(CheckBalance criteria) {

        return UserResult.CheckBalance.fromInfo(userService.findUserBalance(criteria.userId()));

    }

    // 잔액충전
    @Transactional
    @Retryable(
        retryFor = {ObjectOptimisticLockingFailureException.class},
        maxAttempts = 20, // 재실행 횟수(낙관적 락 충돌시에만 재실행), default는 3회
        backoff = @Backoff(50), // 0.05초 간격으로 재실행
        listeners = {"retryLoggingListener"} // 재시도 시 리스너(로그) 실행 - 한 개의 쓰레드를 점유한다.
    )
    public UserResult.ChargeBalance chargeBalance(ChargeBalance criteria){
        // 충전
        return UserResult.ChargeBalance.fromInfo(userService.chargeUserBalance(criteria.toCommand()));

    }




}
