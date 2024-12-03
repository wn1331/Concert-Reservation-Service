package hhplus.concertreservationservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hhplus.concertreservationservice.domain.user.dto.UserCommand;
import hhplus.concertreservationservice.domain.user.dto.UserInfo;
import hhplus.concertreservationservice.domain.user.entity.User;
import hhplus.concertreservationservice.domain.user.entity.UserPointHistory;
import hhplus.concertreservationservice.domain.user.entity.UserPointHistoryType;
import hhplus.concertreservationservice.domain.user.repository.UserPointHistoryRepository;
import hhplus.concertreservationservice.domain.user.repository.UserRepository;
import hhplus.concertreservationservice.domain.user.service.UserService;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("[단위 테스트] UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPointHistoryRepository userPointHistoryRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    private final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .point(BigDecimal.valueOf(50000))
            .name("Test User")
            .build();


        // spy를 통해 user 객체를 감시하고 ID 값을 강제로 설정
        user = spy(user);
    }


    @Test
    @Order(1)
    @DisplayName("[성공] 유저 잔액 조회")
    void testFindUserBalance_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(user.getId()).thenReturn(USER_ID);

        // When
        UserInfo.CheckBalance result = userService.findUserBalance(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.userId());  // userId 확인
        assertEquals(BigDecimal.valueOf(50000), result.balance());  // 포인트 확인
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @Order(2)
    @DisplayName("[실패] 유저 잔액 조회 - 유저 없음")
    void testFindUserBalance_UserNotFound() {
        // Given
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        // When & Then
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> {
            userService.findUserBalance(USER_ID);
        });

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(userRepository, times(1)).findById(USER_ID);
    }

    @Test
    @Order(3)
    @DisplayName("[성공] 유저 존재 여부 확인")
    void testExistCheckUser_Success() {
        // Given
        when(userRepository.existsById(USER_ID)).thenReturn(true);

        // When
        userService.existCheckUser(USER_ID);

        // Then
        verify(userRepository, times(1)).existsById(USER_ID);
    }

    @Test
    @Order(4)
    @DisplayName("[실패] 유저 존재 여부 확인 - 유저 없음")
    void testExistCheckUser_UserNotFound() {
        // Given
        when(userRepository.existsById(USER_ID)).thenReturn(false);

        // When & Then
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> {
            userService.existCheckUser(USER_ID);
        });

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(userRepository, times(1)).existsById(USER_ID);
    }

    @Test
    @Order(5)
    @DisplayName("[성공] 유저 잔액 충전")
    void testChargeUserBalance_Success() {
        when(user.getId()).thenReturn(USER_ID);

        // Given
        UserCommand.ChargeBalance command = new UserCommand.ChargeBalance(USER_ID,
            BigDecimal.valueOf(10000));

        when(userRepository.findByIdForChargePoint(USER_ID)).thenReturn(Optional.of(user));


        // When
        UserInfo.ChargeBalance result = userService.chargeUserBalance(command);


        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(60000), result.balance());  // 충전 후 잔액 확인 50000 + 10000

        ArgumentCaptor<UserPointHistory> captor = ArgumentCaptor.forClass(UserPointHistory.class);
        verify(userPointHistoryRepository, times(1)).save(captor.capture());

        // 캡처된 객체의 필드 값 확인
        UserPointHistory savedHistory = captor.getValue();
        assertEquals(USER_ID, savedHistory.getUserId());
        assertEquals(BigDecimal.valueOf(10000), savedHistory.getRequestPoint());
        assertEquals(UserPointHistoryType.CHARGE, savedHistory.getType());

        verify(userRepository, times(1)).findByIdForChargePoint(USER_ID);
    }

    @Test
    @Order(6)
    @DisplayName("[실패] 유저 잔액 충전 - 유저 없음")
    void testChargeUserBalance_UserNotFound() {
        // Given
        UserCommand.ChargeBalance command = new UserCommand.ChargeBalance(USER_ID, BigDecimal.valueOf(10000));
        when(userRepository.findByIdForChargePoint(USER_ID)).thenReturn(Optional.empty());

        // When & Then
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> {
            userService.chargeUserBalance(command);
        });
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(userRepository, times(1)).findByIdForChargePoint(USER_ID);
        verify(userPointHistoryRepository, never()).save(any(UserPointHistory.class));
    }

    @Test
    @Order(7)
    @DisplayName("[성공] 유저 결제")
    void testUserPayReservation_Success() {
        // Given
        User user = spy(User.builder().point(BigDecimal.valueOf(50000)).build());
        UserCommand.UserPay command = new UserCommand.UserPay(USER_ID, BigDecimal.valueOf(30000));

        when(userRepository.findByIdForUsePoint(USER_ID)).thenReturn(Optional.of(user));
        when(user.getPoint()).thenReturn(BigDecimal.valueOf(50000));

        // When
        userService.userPayReservation(command);

        // Then
        verify(user, times(1)).pointUse(BigDecimal.valueOf(30000));
    }

    @Test
    @Order(8)
    @DisplayName("[실패] 유저 결제 - 잔액 부족")
    void testUserPayReservation_NotEnoughBalance() {
        // Given
        User user = spy(User.builder().point(BigDecimal.valueOf(10000)).build());
        UserCommand.UserPay command = new UserCommand.UserPay(USER_ID, BigDecimal.valueOf(30000));

        when(userRepository.findByIdForUsePoint(USER_ID)).thenReturn(Optional.of(user));
        when(user.getPoint()).thenReturn(BigDecimal.valueOf(10000));

        // When & Then
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> {
            userService.userPayReservation(command);
        });

        assertEquals(ErrorCode.NOT_ENOUGH_BALANCE, exception.getErrorCode());
        verify(user, never()).pointUse(any(BigDecimal.class));
        verify(userPointHistoryRepository, never()).save(any(UserPointHistory.class));
    }

    @Test
    @Order(9)
    @DisplayName("[실패] 유저 결제 - 유저 없음")
    void testUserPayReservation_UserNotFound() {
        // Given
        UserCommand.UserPay command = new UserCommand.UserPay(USER_ID, BigDecimal.valueOf(30000));
        when(userRepository.findByIdForUsePoint(USER_ID)).thenReturn(Optional.empty());

        // When & Then
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> {
            userService.userPayReservation(command);
        });

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(userPointHistoryRepository, never()).save(any(UserPointHistory.class));
    }

}
