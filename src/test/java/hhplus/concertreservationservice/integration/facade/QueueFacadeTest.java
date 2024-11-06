package hhplus.concertreservationservice.integration.facade;

import hhplus.concertreservationservice.application.queue.dto.QueueCriteria;
import hhplus.concertreservationservice.application.queue.dto.QueueResult;
import hhplus.concertreservationservice.application.queue.facade.QueueFacade;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@DisplayName("[통합 테스트] QueueFacade 테스트")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class QueueFacadeTest {

    @Autowired
    private QueueFacade queueFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QueueRepository queueRepository;

    private User user;

    @BeforeEach
    void setup() {
        // 테스트 유저 데이터 생성
        user = new User("테스트 유저", BigDecimal.valueOf(100000));
        userRepository.save(user);

    }

    @Test
    @Order(1)
    @DisplayName("[성공] 대기열 등록 및 토큰 발급 테스트")
    void enqueue_success() {
        // Given: 대기열 등록을 위한 기준
        QueueCriteria.Enqueue criteria = QueueCriteria.Enqueue.builder()
            .userId(user.getId())
            .build();

        // When: 대기열에 등록 및 토큰 발급
        QueueResult.Enqueue result = queueFacade.enqueue(criteria);

        // Then: 발급된 토큰이 null이 아닌지 확인
        assertTrue(result.queueToken() != null && !result.queueToken().isEmpty());

    }

    @Test
    @Order(2)
    @DisplayName("[성공][스케줄러] 대기열 활성화 테스트")
    void activateProcess_success() {

    }

}
