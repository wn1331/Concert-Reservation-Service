package hhplus.concertreservationservice.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import hhplus.concertreservationservice.application.user.dto.UserCriteria;
import hhplus.concertreservationservice.application.user.dto.UserCriteria.ChargeBalance;
import hhplus.concertreservationservice.application.user.dto.UserResult;
import hhplus.concertreservationservice.application.user.facade.UserFacade;
import hhplus.concertreservationservice.domain.queue.entity.Queue;
import hhplus.concertreservationservice.domain.queue.entity.QueueStatusType;
import hhplus.concertreservationservice.domain.queue.repository.QueueRepository;
import hhplus.concertreservationservice.domain.user.entity.User;
import hhplus.concertreservationservice.domain.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.LongStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("[통합 테스트] UserFacade 테스트")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class UserFacadeIntegrationTest {


    @Autowired
    private UserFacade facade;

    @Test
    @Order(1)
    @DisplayName("[동시성 테스트] 10번의 10000원 충전 요청이 동시에 일어날 때, 10만원이 들어오는지 테스트")
    void chargeBalance_concurrency_test() {


        ChargeBalance dto = ChargeBalance.builder().userId(1L).amount(BigDecimal.valueOf(10000))
            .queueToken("3b93aaaf-0ea8-49e4-be70-574a1813167b").build();

        List<CompletableFuture<UserResult.ChargeBalance>> tasks = new ArrayList<>();

        // 동시에 10번의 충전 요청을 수행
        for (long i = 1; i <= 10; i++) {
            tasks.add(CompletableFuture.supplyAsync(() -> facade.chargeBalance(dto)));
        }

        CompletableFuture<Void> allTasks = CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0]));
        allTasks.join();


        // 충전 후의 최종 금액 확인 (Mock으로 가정)
        BigDecimal expectedTotalAmount = new BigDecimal(100000);  // 총 10만원 (기존에 0원 있던 User 1번)
        BigDecimal actualTotalAmount = facade.checkBalance(UserCriteria.CheckBalance.builder()
                .userId(1L)
                .queueToken("3b93aaaf-0ea8-49e4-be70-574a1813167b")
            .build()).balance();  // 최종 잔액 확인

        assertEquals(0, expectedTotalAmount.compareTo(actualTotalAmount));  // 최종 잔액 검증
    }

}
