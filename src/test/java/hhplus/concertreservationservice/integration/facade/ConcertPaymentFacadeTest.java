package hhplus.concertreservationservice.integration.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import hhplus.concertreservationservice.application.concert.dto.ConcertCriteria;
import hhplus.concertreservationservice.application.concert.dto.ConcertResult;
import hhplus.concertreservationservice.application.concert.facade.ConcertPaymentFacade;
import hhplus.concertreservationservice.domain.concert.dto.ConcertPaymentSuccessEvent;
import hhplus.concertreservationservice.domain.concert.entity.Concert;
import hhplus.concertreservationservice.domain.concert.entity.ConcertReservation;
import hhplus.concertreservationservice.domain.concert.entity.ConcertSchedule;
import hhplus.concertreservationservice.domain.concert.entity.ConcertSeat;
import hhplus.concertreservationservice.domain.concert.entity.ReservationStatusType;
import hhplus.concertreservationservice.domain.concert.entity.SeatStatusType;
import hhplus.concertreservationservice.domain.concert.repository.ConcertRepository;
import hhplus.concertreservationservice.domain.concert.repository.ConcertReservationRepository;
import hhplus.concertreservationservice.domain.concert.repository.ConcertScheduleRepository;
import hhplus.concertreservationservice.domain.concert.repository.ConcertSeatRepository;
import hhplus.concertreservationservice.domain.queue.repository.QueueRepository;
import hhplus.concertreservationservice.domain.user.entity.User;
import hhplus.concertreservationservice.domain.user.repository.UserPointHistoryRepository;
import hhplus.concertreservationservice.domain.user.repository.UserRepository;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("[통합 테스트] ConcertPaymentFacade 테스트")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ConcertPaymentFacadeTest {

    @Autowired
    private ConcertPaymentFacade concertPaymentFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ConcertScheduleRepository concertScheduleRepository;

    @Autowired
    private ConcertSeatRepository concertSeatRepository;

    @Autowired
    private ConcertReservationRepository concertReservationRepository;

    @Autowired
    private UserPointHistoryRepository userPointHistoryRepository;

    private User user;
    private Concert concert;
    private ConcertSeat seat;
    private ConcertReservation concertReservation;
    private ConcertSchedule concertSchedule;


    @BeforeEach
    void setup() {
        // 테스트 유저 생성
        user = new User("테스트 유저", BigDecimal.valueOf(1000000));
        userRepository.save(user);

        // 콘서트와 스케줄, 좌석 생성
        concert = new Concert("테스트 콘서트");
        concertRepository.save(concert);

        concertSchedule = new ConcertSchedule(concert.getId(), LocalDate.of(2024, 12, 1));
        concertScheduleRepository.save(concertSchedule);

        // 콘서트 좌석 생성
        ConcertSeat seat1 = new ConcertSeat(concertSchedule.getId(), "A1",
            BigDecimal.valueOf(150000),
            SeatStatusType.EMPTY);
        seat = new ConcertSeat(concertSchedule.getId(), "A2",
            BigDecimal.valueOf(150000),
            SeatStatusType.RESERVED);
        List<ConcertSeat> seats = List.of(
            seat1,
            seat
        );
        concertSeatRepository.saveAll(seats);

        concertReservation = new ConcertReservation(user.getId(),
            4L, BigDecimal.valueOf(150000),
            ReservationStatusType.RESERVED);
        concertReservationRepository.save(concertReservation);
    }

    @Test
    @Order(1)
    @DisplayName("[성공] 콘서트 결제 테스트")
    void pay_success() {

        // Given
        ConcertCriteria.Pay criteria = ConcertCriteria.Pay.builder()
            .userId(user.getId())
            .reservationId(1L)
            .build();
        // When
        ConcertResult.Pay result = concertPaymentFacade.pay(criteria);

        // Then
        assertEquals(1L, result.paymentId());  // 결제가 성공했는지 확인
    }

    @Test
    @Order(1)
    @DisplayName("[실패] 예약이 존재하지 않아 결제 실패")
    void pay_failure_reservation_not_found() {
        // Given
        ConcertCriteria.Pay criteria = ConcertCriteria.Pay.builder()
            .userId(user.getId())
            .reservationId(999L)  // 존재하지 않는 예약 ID
            .build();

        // When / Then
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> {
            concertPaymentFacade.pay(criteria);
        });

        // 예외 코드 확인
        assertEquals(ErrorCode.CONCERT_RESERVATION_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @Order(2)
    @Transactional
    @DisplayName("[실패] 이미 결제된 예약으로 인한 결제 실패")
    void pay_failure_already_paid_or_cancelled() {
        // Given
        concertReservation.confirmPayment(); // 예약 상태를 결제 완료로 변경

        ConcertCriteria.Pay criteria = ConcertCriteria.Pay.builder()
            .userId(user.getId())
            .reservationId(concertReservation.getId())
            .build();

        // When / Then
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> {
            concertPaymentFacade.pay(criteria);
        });

        // 예외 코드 확인
        assertEquals(ErrorCode.ALREADY_PAID_OR_CANCELLED, exception.getErrorCode());
    }

    @Test
    @Order(3)
    @DisplayName("[실패] 사용자 존재하지 않아 결제 실패")
    void pay_failure_user_not_found() {
        // Given
        ConcertCriteria.Pay criteria = ConcertCriteria.Pay.builder()
            .userId(123L) // 없는 유저.
            .reservationId(concertReservation.getId())
            .build();

        // When / Then
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> {
            concertPaymentFacade.pay(criteria);
        });

        // 예외 코드 확인
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }


    @Test
    @Order(4)
    @Transactional
    @DisplayName("[실패] 잔액 부족으로 인한 결제 실패")
    void pay_failure_not_enough_balance() {
        // Given
        concertReservation = new ConcertReservation(user.getId(),
            302L, BigDecimal.valueOf(15000000),
            ReservationStatusType.RESERVED);
        concertReservationRepository.save(concertReservation);

        ConcertCriteria.Pay criteria = ConcertCriteria.Pay.builder()
            .userId(user.getId())
            .reservationId(concertReservation.getId())
            .build();

        // When / Then
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> {
            concertPaymentFacade.pay(criteria);
        });

        // 예외 코드 확인
        assertEquals(ErrorCode.NOT_ENOUGH_BALANCE, exception.getErrorCode());
    }


    @Test
    @Order(5)
    @DisplayName("[실패] 좌석을 찾을 수 없어 결제 실패")
    void pay_failure_seat_not_found() {
        // 없는 좌석 번호.
        concertReservation = new ConcertReservation(user.getId(),
            999L, BigDecimal.valueOf(150000),
            ReservationStatusType.RESERVED);
        concertReservationRepository.save(concertReservation);
        // Given
        ConcertCriteria.Pay criteria = ConcertCriteria.Pay.builder()
            .userId(user.getId())
            .reservationId(concertReservation.getId())
            .build();

        // When / Then
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> {
            concertPaymentFacade.pay(criteria);
        });

        // 예외 코드 확인
        assertEquals(ErrorCode.CONCERT_SEAT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @Order(6)
    @Transactional
    @DisplayName("[실패] 좌석이 예약되지 않아 결제 실패")
    void pay_failure_seat_not_reserved() {
        // Given
        seat.confirmSeatByPayment();
        ConcertCriteria.Pay criteria = ConcertCriteria.Pay.builder()
            .userId(user.getId())
            .reservationId(concertReservation.getId())
            .build();

        // When / Then
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> {
            concertPaymentFacade.pay(criteria);
        });

        // 예외 코드 확인
        assertEquals(ErrorCode.SEAT_NOT_RESERVED, exception.getErrorCode());
    }

    @Test
    @Order(7)
    @DisplayName("[성공] 트랜잭션 커밋 후에 유저 결제 이력 저장 이벤트 리스너가 호출되는지 검증")
    void testSaveUserPaymentHistoryAfterCommit() {

        // Given
        ConcertCriteria.Pay criteria = ConcertCriteria.Pay.builder()
            .userId(user.getId())
            .reservationId(1L)
            .build();
        // When
        ConcertResult.Pay result = concertPaymentFacade.pay(criteria);


        // Then
        assertEquals(1L, result.paymentId());  // 결제가 성공했는지 확인


        // 유저 결제 이력 저장 이벤트 리스너가 결제 이력을 저장했는지 확인
        assertTrue(userPointHistoryRepository.existsByUserId(user.getId()),
            "이벤트 리스너가 결제 이력을 저장해야 합니다.");

    }


}
