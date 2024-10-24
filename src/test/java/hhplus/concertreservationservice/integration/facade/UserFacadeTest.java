package hhplus.concertreservationservice.integration.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;

import hhplus.concertreservationservice.application.user.dto.UserCriteria;
import hhplus.concertreservationservice.application.user.dto.UserResult;
import hhplus.concertreservationservice.application.user.facade.UserFacade;
import hhplus.concertreservationservice.domain.queue.entity.Queue;
import hhplus.concertreservationservice.domain.queue.entity.QueueStatusType;
import hhplus.concertreservationservice.domain.queue.repository.QueueRepository;
import hhplus.concertreservationservice.domain.user.entity.User;
import hhplus.concertreservationservice.domain.user.repository.UserRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@DisplayName("[통합 테스트] UserFacade 테스트")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserFacadeTest {

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setup() {
        // 테스트 사용자 생성 및 저장
        user = new User("테스트 유저", BigDecimal.valueOf(100000));  // 잔액 100,000원
        userRepository.save(user);

    }

    @Test
    @Order(1)
    @DisplayName("[성공] 잔액 조회 테스트 - 대기열 통과한 사용자")
    void checkBalance_success() {
        // Given
        UserCriteria.CheckBalance criteria = UserCriteria.CheckBalance.builder()
            .userId(user.getId())
            .build();

        // When
        UserResult.CheckBalance result = userFacade.checkBalance(criteria);

        // Then
        assertEquals(user.getId(), result.userId());
        assertEquals(0, BigDecimal.valueOf(100000).compareTo(result.balance()));  // 잔액 검증
    }

    @Test
    @Order(2)
    @DisplayName("[성공] 잔액 충전 테스트 - 대기열 통과한 사용자")
    void chargeBalance_success() {
        // Given
        UserCriteria.ChargeBalance criteria = UserCriteria.ChargeBalance.builder()
            .userId(user.getId())
            .amount(BigDecimal.valueOf(50000))  // 50,000원 충전
            .build();

        // When
        UserResult.ChargeBalance result = userFacade.chargeBalance(criteria);

        // Then
        assertEquals(user.getId(), result.userId());
        assertEquals(0, BigDecimal.valueOf(150000).compareTo(result.balance()));  // 최종 잔액 150,000원 확인
    }
}
