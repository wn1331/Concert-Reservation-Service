package hhplus.concertreservationservice.integration.concurrency;

import static org.junit.jupiter.api.Assertions.assertEquals;

import hhplus.concertreservationservice.application.concert.dto.ConcertResult;
import hhplus.concertreservationservice.application.user.dto.UserCriteria;
import hhplus.concertreservationservice.application.user.dto.UserCriteria.ChargeBalance;
import hhplus.concertreservationservice.application.user.dto.UserResult;
import hhplus.concertreservationservice.application.user.facade.UserFacade;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("[통합 테스트] User 동시성 테스트")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class UserConcurrencyTest {


    private static final Logger log = LoggerFactory.getLogger(UserConcurrencyTest.class);
    @Autowired
    private UserFacade facade;

    @Test
    @Order(1)
    @DisplayName("[동시성 테스트] 10번의 10000원 충전 요청이 동시에 일어날 때, 10만원이 들어오는지 테스트")
    void chargeBalance_concurrency_test() {

        ChargeBalance dto = ChargeBalance.builder().userId(1L).amount(BigDecimal.valueOf(10000))
            .build();

        List<CompletableFuture<UserResult.ChargeBalance>> tasks = new ArrayList<>();

        // 동시에 10번의 충전 요청을 수행
        for (long i = 1; i <= 10; i++) {

            tasks.add(CompletableFuture.supplyAsync(() -> {
                try {
                    UserResult.ChargeBalance chargeBalance = facade.chargeBalance(dto);
                    log.info("충전 성공");
                    return chargeBalance;
                    // 재시도 로직으로 낙관락 예외는 잡히지 않는다.
                } catch (Exception e) {
                    log.error("예외 : {}", e.getMessage());
                    return null;
                }
            }));
        }

        CompletableFuture<Void> allTasks = CompletableFuture.allOf(
            tasks.toArray(new CompletableFuture[0]));
        allTasks.join();

        // 충전 후의 최종 금액 확인 (Mock으로 가정)
        BigDecimal expectedTotalAmount = new BigDecimal(100000);  // 총 10만원 (기존에 0원 있던 User 1번)
        BigDecimal actualTotalAmount = facade.checkBalance(UserCriteria.CheckBalance.builder()
            .userId(1L)
            .build()).balance();  // 최종 잔액 확인

        assertEquals(0, expectedTotalAmount.compareTo(actualTotalAmount));  // 최종 잔액 검증
    }

}
