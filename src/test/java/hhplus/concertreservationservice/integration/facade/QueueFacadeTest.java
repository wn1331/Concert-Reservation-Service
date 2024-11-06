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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
        // Given: 대기열 생성
        QueueCriteria.Enqueue enqueueCriteria = QueueCriteria.Enqueue.builder()
            .userId(user.getId())
            .build();
        QueueResult.Enqueue enqueueResult = queueFacade.enqueue(enqueueCriteria);


        // When: 대기열 활성화 프로세스 실행
        queueFacade.activateProcess();

        // Then: QueueRepository의 활성화된 대기열이 존재하는지 확인
        boolean isActivated = queueRepository.existActiveToken(enqueueResult.queueToken());
        assertTrue(isActivated);
    }

    @Test
    @Order(3)
    @DisplayName("[성공] 대기열 검증 테스트")
    void queueValidation_success() {
        // Given: 대기열에 토큰을 발급받은 후 활성화 프로세스를 태우고 검증을 진행
        QueueCriteria.Enqueue enqueueCriteria = QueueCriteria.Enqueue.builder()
            .userId(user.getId())
            .build();
        // 대기열 생성
        QueueResult.Enqueue enqueueResult = queueFacade.enqueue(enqueueCriteria);
        // 대기열 통과
        queueFacade.activateProcess();


        // When: 대기열 검증 실행
        QueueCriteria.VerifyQueue verifyCriteria = QueueCriteria.VerifyQueue.builder()
            .queueToken(enqueueResult.queueToken())
            .build();

        // Then: 대기열 검증이 성공적으로 완료됨을 확인 (예외가 발생하지 않아야 함)
        assertDoesNotThrow(() -> queueFacade.queueValidation(verifyCriteria));

    }

    @Test
    @Order(4)
    @DisplayName("[성공] 대기열 순번 조회 테스트")
    void getQueueOrder_success() {
        // Given: 대기열에 등록 및 토큰 발급
        QueueCriteria.Enqueue enqueueCriteria = QueueCriteria.Enqueue.builder()
            .userId(user.getId())
            .build();
        QueueResult.Enqueue enqueueResult = queueFacade.enqueue(enqueueCriteria);

        // When: 대기열 순번 조회
        QueueCriteria.Order orderCriteria = new QueueCriteria.Order(enqueueResult.queueToken());
        QueueResult.Order result = queueFacade.getQueueOrder(orderCriteria);

        // Then: 조회된 순번이 0 이상의 값임을 확인
        // 이 테스트는 기본적으로 순번 조회가 정상적으로 수행되었는지 확인하는 데 중점을 둠
        assertTrue(result.order() >= 0);
    }


}
