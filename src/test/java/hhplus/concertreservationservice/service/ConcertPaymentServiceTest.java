package hhplus.concertreservationservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hhplus.concertreservationservice.domain.concert.dto.ConcertCommand.Pay;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo;
import hhplus.concertreservationservice.domain.concert.entity.ConcertPayment;
import hhplus.concertreservationservice.domain.concert.entity.PaymentStatusType;
import hhplus.concertreservationservice.domain.concert.repository.ConcertPaymentRepository;
import hhplus.concertreservationservice.domain.concert.service.ConcertPaymentService;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("[단위 테스트] ConcertPaymentService")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConcertPaymentServiceTest {

    @Mock
    private ConcertPaymentRepository concertPaymentRepository;

    @InjectMocks
    private ConcertPaymentService concertPaymentService;

    private ConcertPayment payment;

    @BeforeEach
    void setUp() {
        payment = ConcertPayment.builder()
            .reservationId(1L)
            .price(BigDecimal.valueOf(150000))
            .status(PaymentStatusType.SUCCEED)
            .build();

        // spy를 통해 객체를 감시하고 ID 값을 강제로 설정 가능
        payment = spy(payment);
    }

    @Test
    @DisplayName("[성공] 예약 결제 처리")
    void payReservation_Success() {
        // Given
        Pay command = new Pay(1L, BigDecimal.valueOf(150000));
        when(payment.getId()).thenReturn(1L);

        // Repository가 ConcertPayment를 저장하고 저장된 객체를 반환하도록 Mock 설정
        when(concertPaymentRepository.save(any(ConcertPayment.class))).thenReturn(payment);

        // When
        ConcertInfo.Pay result = concertPaymentService.payReservation(command);

        // Then
        assertEquals(1L, result.paymentId()); // 반환된 paymentId가 예상과 일치하는지 확인
        verify(concertPaymentRepository, times(1)).save(any(ConcertPayment.class)); // save 호출 여부 검증
    }
}
