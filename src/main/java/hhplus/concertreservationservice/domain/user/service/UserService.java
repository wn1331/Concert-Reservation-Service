package hhplus.concertreservationservice.domain.user.service;

import hhplus.concertreservationservice.domain.concert.event.ConcertPaymentSuccessEvent;
import hhplus.concertreservationservice.domain.user.dto.UserCommand;
import hhplus.concertreservationservice.domain.user.dto.UserCommand.UserPay;
import hhplus.concertreservationservice.domain.user.dto.UserInfo;
import hhplus.concertreservationservice.domain.user.entity.User;
import hhplus.concertreservationservice.domain.user.entity.UserPointHistory;
import hhplus.concertreservationservice.domain.user.entity.UserPointHistoryType;
import hhplus.concertreservationservice.domain.user.repository.UserPointHistoryRepository;
import hhplus.concertreservationservice.domain.user.repository.UserRepository;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserPointHistoryRepository userPointHistoryRepository;


    public UserInfo.CheckBalance findUserBalance(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomGlobalException(ErrorCode.USER_NOT_FOUND));
        return UserInfo.CheckBalance.builder()
            .userId(user.getId())
            .balance(user.getPoint())
            .build();
    }


    public void existCheckUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new CustomGlobalException(ErrorCode.USER_NOT_FOUND);
        }
    }


    @Transactional
    public UserInfo.ChargeBalance chargeUserBalance(UserCommand.ChargeBalance command){
        User user = userRepository.findByIdForChargePoint(command.userId())
           .orElseThrow(() -> new CustomGlobalException(ErrorCode.USER_NOT_FOUND));

        user.pointCharge(command.amount());

        userPointHistoryRepository.save(UserPointHistory.builder()
            .userId(command.userId())
            .type(UserPointHistoryType.CHARGE)
            .requestPoint(command.amount())
            .build());

        return UserInfo.ChargeBalance.fromEntity(user);
    }

    public void userPayReservation(UserPay command) {
        User user = userRepository.findByIdForUsePoint(command.userId())
            .orElseThrow(() -> new CustomGlobalException(ErrorCode.USER_NOT_FOUND));

        if (user.getPoint().compareTo(command.price()) > 0) {
            // 변경감지. 포인트 사용
            user.pointUse(command.price());

        } else {
            throw new CustomGlobalException(ErrorCode.NOT_ENOUGH_BALANCE);
        }
    }

    public void saveUserPaymentHistory(ConcertPaymentSuccessEvent event){
        userPointHistoryRepository.save(UserPointHistory.builder()
            .userId(event.userId())
            .requestPoint(event.price())
            .type(UserPointHistoryType.PAY)
            .build()
        );

    }

}
