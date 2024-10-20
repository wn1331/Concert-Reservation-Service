package hhplus.concertreservationservice.application.user.facade;

import hhplus.concertreservationservice.application.user.dto.UserCriteria.ChargeBalance;
import hhplus.concertreservationservice.application.user.dto.UserCriteria.CheckBalance;
import hhplus.concertreservationservice.application.user.dto.UserResult;
import hhplus.concertreservationservice.domain.queue.service.QueueService;
import hhplus.concertreservationservice.domain.queue.service.QueueVerifyType;
import hhplus.concertreservationservice.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
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
    public UserResult.ChargeBalance chargeBalance(ChargeBalance criteria){
        // 충전
        return UserResult.ChargeBalance.fromInfo(userService.chargeUserBalance(criteria.toCommand()));

    }




}
