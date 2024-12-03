package hhplus.concertreservationservice.subscriber;
import hhplus.concertreservationservice.domain.concert.event.ConcertPaymentSuccessEvent;
import hhplus.concertreservationservice.domain.user.service.UserService;
import hhplus.concertreservationservice.domain.user.event.subscriber.UserPointHistoryEventListener;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.verify;

@DisplayName("[단위 테스트] UserPointHistoryEventListener")
class UserPointHistoryEventListenerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserPointHistoryEventListener eventListener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("유저 결제 이력 저장 이벤트 리스너 메서드 호출 테스트")
    void testSaveUserPaymentHistoryListener() {
        // Given
        ConcertPaymentSuccessEvent event = new ConcertPaymentSuccessEvent(1L, BigDecimal.valueOf(1000), 1L,1L, "EXAMPLE-TOKEN");

        // When
        eventListener.saveUserPaymentHistory(event); // 직접 메서드 호출

        // Then
        verify(userService).saveUserPaymentHistory(event);
    }
}

