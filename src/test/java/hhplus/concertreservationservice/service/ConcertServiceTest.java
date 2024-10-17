package hhplus.concertreservationservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hhplus.concertreservationservice.domain.concert.dto.ConcertCommand;
import hhplus.concertreservationservice.domain.concert.dto.ConcertCommand.Pay;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo;
import hhplus.concertreservationservice.domain.concert.entity.ConcertPayment;
import hhplus.concertreservationservice.domain.concert.entity.ConcertReservation;
import hhplus.concertreservationservice.domain.concert.entity.ConcertSchedule;
import hhplus.concertreservationservice.domain.concert.entity.ConcertSeat;
import hhplus.concertreservationservice.domain.concert.entity.PaymentStatusType;
import hhplus.concertreservationservice.domain.concert.entity.ReservationStatusType;
import hhplus.concertreservationservice.domain.concert.entity.SeatStatusType;
import hhplus.concertreservationservice.domain.concert.repository.ConcertPaymentRepository;
import hhplus.concertreservationservice.domain.concert.repository.ConcertReservationRepository;
import hhplus.concertreservationservice.domain.concert.repository.ConcertScheduleRepository;
import hhplus.concertreservationservice.domain.concert.repository.ConcertSeatRepository;
import hhplus.concertreservationservice.domain.concert.service.ConcertService;
import hhplus.concertreservationservice.domain.queue.entity.Queue;
import hhplus.concertreservationservice.domain.queue.entity.QueueStatusType;
import hhplus.concertreservationservice.domain.queue.repository.QueueRepository;
import hhplus.concertreservationservice.domain.user.entity.User;
import hhplus.concertreservationservice.domain.user.entity.UserPointHistory;
import hhplus.concertreservationservice.domain.user.repository.UserPointHistoryRepository;
import hhplus.concertreservationservice.domain.user.repository.UserRepository;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.DisplayName;


@ExtendWith(MockitoExtension.class)
@DisplayName("[단위 테스트] ConcertController")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConcertServiceTest {


    @Mock
    private QueueRepository queueRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ConcertPaymentRepository concertPaymentRepository;

    @Mock
    private ConcertReservationRepository concertReservationRepository;

    @Mock
    private UserPointHistoryRepository userPointHistoryRepository;

    @Mock
    private ConcertScheduleRepository concertScheduleRepository;

    @Mock
    private ConcertSeatRepository concertSeatRepository;

    @InjectMocks
    private ConcertService concertService;

    private ConcertPayment payment;
    private ConcertReservation reservation;
    private ConcertSchedule concertSchedule;
    private ConcertSeat concertSeat;
    private User user;
    private Queue queue;

    private final Long RESERVATION_ID = 1L;
    private final Long CONCERTSEAT_ID = 1L;
    private final Long USER_ID = 1L;
    private final String QUEUE_TOKEN = "57122b5d-81aa-4bf3-b116-a1a9bf3e9348";
    @BeforeEach
    void setUp() {
        user = User.builder()
            .point(BigDecimal.valueOf(500000))
            .name("Test User")
            .build();

        concertSchedule = ConcertSchedule.builder()
            .concertId(1L)
            .concertDateTime(LocalDate.of(2024, 10, 17))
            .build();

        concertSeat = ConcertSeat.builder()
            .concertScheduleId(1L)
            .seatNum("E01")
            .price(BigDecimal.valueOf(150000))
            .status(SeatStatusType.EMPTY)
            .build();

        reservation = ConcertReservation.builder()
            .userId(1L)
            .price(BigDecimal.valueOf(150000))
            .concertSeatId(1L)
            .status(ReservationStatusType.RESERVED)
            .build();

        payment = ConcertPayment.builder()
            .reservationId(1L)
            .price(BigDecimal.valueOf(150000))
            .status(PaymentStatusType.SUCCEED)
            .build();

        queue = Queue.builder()
            .userId(USER_ID)
            .queueToken(QUEUE_TOKEN)
            .status(QueueStatusType.WAITING)
            .build();


        // spy를 통해 객체를 감시하고 ID 값을 강제로 설정 가능

        payment = spy(payment);
        concertSchedule = spy(concertSchedule);
        reservation = spy(reservation);
        concertSeat = spy(concertSeat);
        user = spy(user);
        queue = spy(queue);

    }

    @Test
    @Order(1)
    @DisplayName("[성공] 예약 가능한 일정 조회")
    void testGetAvailableSchedules_Success() {
        // Given
        List<ConcertSchedule> mockSchedules = List.of(
            new ConcertSchedule(1L, LocalDate.of(2024, 10, 17)),
            new ConcertSchedule(2L, LocalDate.of(2024, 10, 18))
        );
        when(concertScheduleRepository.findByConcertIdAndConcertDateAfter(anyLong())).thenReturn(
            mockSchedules);

        // When
        ConcertCommand.GetAvailableSchedules command = new ConcertCommand.GetAvailableSchedules(1L);
        ConcertInfo.AvailableSchedules result = concertService.getAvailableSchedules(command);

        // Then
        assertNotNull(result);
        assertEquals(2, result.schedules().size());
        verify(concertScheduleRepository, times(1)).findByConcertIdAndConcertDateAfter(anyLong());
    }

    @Test
    @Order(2)
    @DisplayName("[성공] 예약 가능한 좌석 조회")
    void testGetAvailableSeats_Success() {
        // Given
        List<ConcertSeat> mockSeats = List.of(
            new ConcertSeat(1L, "E01", BigDecimal.valueOf(150000), SeatStatusType.EMPTY),
            new ConcertSeat(2L, "E02", BigDecimal.valueOf(140000), SeatStatusType.EMPTY)
        );
        when(concertSeatRepository.findByConcertScheduleIdAndStatus(anyLong())).thenReturn(
            mockSeats);

        // When
        ConcertCommand.GetAvailableSeats command = new ConcertCommand.GetAvailableSeats(1L);
        ConcertInfo.AvailableSeats result = concertService.getAvailableSeats(command);

        // Then
        assertNotNull(result);
        assertEquals(2, result.seats().size());

        verify(concertSeatRepository, times(1)).findByConcertScheduleIdAndStatus(anyLong());
    }

    @Test
    @Order(3)
    @DisplayName("[성공] 좌석예약(reserveSeat 메서드)")
    void testReserveSeat_Success() {
        // Given: 좌석 상태가 EMPTY로 설정된 Mock 객체
        when(concertSeatRepository.findById(CONCERTSEAT_ID)).thenReturn(Optional.of(concertSeat));
        when(concertSeat.getStatus()).thenReturn(SeatStatusType.EMPTY);  // 좌석 상태가 EMPTY
        when(concertReservationRepository.save(any(ConcertReservation.class))).thenReturn(reservation);  // 예약 저장
        when(reservation.getId()).thenReturn(RESERVATION_ID);
        // When: 좌석 예약 요청을 실행
        ConcertCommand.ReserveSeat command = new ConcertCommand.ReserveSeat(USER_ID, CONCERTSEAT_ID);
        ConcertInfo.ReserveSeat result = concertService.reserveSeat(command);

        // Then: 예약 결과 검증
        assertNotNull(result);  // 예약 결과가 null이 아니어야 함
        assertEquals(RESERVATION_ID, result.reservationId());  // 반환된 예약 ID가 예상한 값과 일치하는지 확인

        // 리포지토리 호출 횟수 검증
        verify(concertSeatRepository, times(1)).findById(CONCERTSEAT_ID);  // 좌석 조회가 한 번 호출되었는지 확인
        verify(concertReservationRepository, times(1)).save(any(ConcertReservation.class));  // 예약 저장이 한 번 호출되었는지 확인
    }

    @Test
    @Order(4)
    @DisplayName("[실패] 좌석예약(reserveSeat 메서드) - 좌석 찾기 실패")
    void testReserveSeat_Failure_SeatNotFound() {
        // Given: 좌석이 조회되지 않음
        when(concertSeatRepository.findById(CONCERTSEAT_ID)).thenReturn(Optional.empty());

        // When & Then: 좌석 조회 실패 시 예외 발생 확인
        ConcertCommand.ReserveSeat command = new ConcertCommand.ReserveSeat(USER_ID, CONCERTSEAT_ID);
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> {
            concertService.reserveSeat(command);
        });

        // 예외가 발생할 때, 에러 코드가 CONCERT_SEAT_NOT_FOUND인지 확인
        assertEquals(ErrorCode.CONCERT_SEAT_NOT_FOUND, exception.getErrorCode());

        // 리포지토리 호출 횟수 검증
        verify(concertSeatRepository, times(1)).findById(CONCERTSEAT_ID);  // 좌석 조회가 한 번 호출되었는지 확인
        verify(concertReservationRepository, times(0)).save(any(ConcertReservation.class));  // 예약 저장은 호출되지 않아야 함
    }



    @Test
    @Order(5)
    @DisplayName("[실패] 좌석예약(reserveSeat 메서드) - 이미 예약된 좌석")
    void testReserveSeat_Failure_AlreadyReserved() {
        // Given: 좌석 상태가 RESERVED로 설정
        when(concertSeatRepository.findById(CONCERTSEAT_ID)).thenReturn(Optional.of(concertSeat));
        when(concertSeat.getStatus()).thenReturn(SeatStatusType.RESERVED);  // 좌석 상태가 이미 예약됨

        // When & Then: 이미 예약된 좌석에 대한 예외 발생 확인
        ConcertCommand.ReserveSeat command = new ConcertCommand.ReserveSeat(USER_ID, CONCERTSEAT_ID);
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> {
            concertService.reserveSeat(command);
        });

        // 예외가 발생할 때, 에러 코드가 ALREADY_RESERVED_SEAT인지 확인
        assertEquals(ErrorCode.ALREADY_RESERVED_SEAT, exception.getErrorCode());

        // 리포지토리 호출 횟수 검증
        verify(concertSeatRepository, times(1)).findById(CONCERTSEAT_ID);  // 좌석 조회가 한 번 호출되었는지 확인
        verify(concertReservationRepository, times(0)).save(any(ConcertReservation.class));  // 예약 저장은 호출되지 않아야 함
    }

    @Test
    @Order(6)
    @DisplayName("[실패] 좌석예약(reserveSeat 메서드) - 이미 판매된 좌석")
    void testReserveSeat_Failure_AlreadySold() {
        // Given: 좌석 상태가 SOLD로 설정
        when(concertSeatRepository.findById(CONCERTSEAT_ID)).thenReturn(Optional.of(concertSeat));
        when(concertSeat.getStatus()).thenReturn(SeatStatusType.SOLD);  // 좌석 상태가 이미 판매됨

        // When & Then: 이미 판매된 좌석에 대한 예외 발생 확인
        ConcertCommand.ReserveSeat command = new ConcertCommand.ReserveSeat(USER_ID, CONCERTSEAT_ID);
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> {
            concertService.reserveSeat(command);
        });

        // 예외가 발생할 때, 에러 코드가 ALREADY_SOLD_SEAT인지 확인
        assertEquals(ErrorCode.ALREADY_SOLD_SEAT, exception.getErrorCode());

        // 리포지토리 호출 횟수 검증
        verify(concertSeatRepository, times(1)).findById(CONCERTSEAT_ID);  // 좌석 조회가 한 번 호출되었는지 확인
        verify(concertReservationRepository, times(0)).save(any(ConcertReservation.class));  // 예약 저장은 호출되지 않아야 함
    }



    @Test
    @Order(7)
    @DisplayName("[성공] 결제(payReservation 메서드)")
    void testPayReservation_Success() {
        // Given :  예약이 정상적으로 조회되어야 하고
        when(concertReservationRepository.findById(RESERVATION_ID)).thenReturn(
            Optional.of(reservation));
        // 유저도 정상적으로 조회되어야 하고 잔액 충분하도록 설정
        when(userRepository.findByIdForUsePoint(USER_ID)).thenReturn(Optional.of(user));
        when(user.getPoint()).thenReturn(BigDecimal.valueOf(1000000));
        // 예약 금액도 적당하도록 설정
        when(reservation.getPrice()).thenReturn(BigDecimal.valueOf(150000));
        // 좌석도 정상적으로 조회되어야 하고
        when(concertSeatRepository.findById(CONCERTSEAT_ID)).thenReturn(Optional.of(concertSeat));
        // 결제도 성공적으로 된다는 설정
        when(concertPaymentRepository.save(any(ConcertPayment.class))).thenReturn(payment);
        // 결제 id도 정상적으로 가져오도록 설정
        when(payment.getId()).thenReturn(1L);

        // 좌석은 예약된 상태로 만들고
        concertSeat.reserveSeat();

        // When : 실제 결제 로직을 수행
        Pay command = new Pay(1L, 1L);
        ConcertInfo.Pay result = concertService.payReservation(command);

        // Then : 결과
        assertNotNull(result);
        assertEquals(1L, result.paymentId());
        verify(concertReservationRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findByIdForUsePoint(anyLong());
        verify(concertSeatRepository, times(1)).findById(anyLong());

        verify(concertPaymentRepository, times(1)).save(any(ConcertPayment.class));
        verify(userPointHistoryRepository, times(1)).save(any(UserPointHistory.class));
    }

    @Test
    @Order(8)
    @DisplayName("[실패] 결제(payReservation 메서드) - 예약 찾기 실패")
    void testPayReservation_Failure_ReservationNotFound() {
        // Given: 예약이 조회되지 않음 (CONCERT_RESERVATION_NOT_FOUND 예외 발생)
        when(concertReservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.empty());

        // When & Then: 예약 조회 실패 시 예외 발생 확인
        Pay command = new Pay(USER_ID, RESERVATION_ID);
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> {
            concertService.payReservation(command);
        });

        // 예외가 발생할 때, 에러 코드가 CONCERT_RESERVATION_NOT_FOUND인지 확인
        assertEquals(ErrorCode.CONCERT_RESERVATION_NOT_FOUND, exception.getErrorCode());

        // 리포지토리 호출 횟수 검증
        verify(concertReservationRepository, times(1)).findById(
            RESERVATION_ID);  // 예약 조회가 한 번 호출되었는지 확인
        verify(userRepository, times(0)).findByIdForUsePoint(USER_ID);  // 유저 조회는 호출되지 않아야 함
        verify(concertSeatRepository, times(0)).findById(CONCERTSEAT_ID);  // 좌석 조회는 호출되지 않아야 함
    }


    @Test
    @Order(9)
    @DisplayName("[실패] 결제(payReservation 메서드) - 사용자 찾기 실패")
    void testPayReservation_Failure_UserNotFound() {
        // Given: 예약은 정상적으로 조회됨
        when(concertReservationRepository.findById(RESERVATION_ID)).thenReturn(
            Optional.of(reservation));

        // 유저가 조회되지 않음 (USER_NOT_FOUND 예외 발생)
        when(userRepository.findByIdForUsePoint(USER_ID)).thenReturn(Optional.empty());

        // When & Then: 유저 조회 실패 시 예외 발생 확인
        Pay command = new Pay(USER_ID, RESERVATION_ID);
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> {
            concertService.payReservation(command);
        });

        // 예외가 발생할 때, 에러 코드가 USER_NOT_FOUND인지 확인
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());

        // 리포지토리 호출 횟수 검증
        verify(concertReservationRepository, times(1)).findById(
            RESERVATION_ID);  // 예약 조회가 한 번 호출되었는지 확인
        verify(userRepository, times(1)).findByIdForUsePoint(USER_ID);  // 유저 조회가 한 번 호출되었는지 확인
        verify(concertSeatRepository, times(0)).findById(CONCERTSEAT_ID);  // 좌석 조회는 호출되지 않아야 함
    }

    @Test
    @Order(10)
    @DisplayName("[실패] 결제(payReservation 메서드) - 결제 시 잔액 부족")
    void testPayReservation_Failure_NotEnoughBalance() {
        // Given: 예약이 정상적으로 조회되고
        when(concertReservationRepository.findById(RESERVATION_ID)).thenReturn(
            Optional.of(reservation));
        // 유저는 정상적으로 조회되어야 한다.
        when(userRepository.findByIdForUsePoint(USER_ID)).thenReturn(Optional.of(user));

        // 유저의 포인트가 부족한 상태로 설정 (getPoint 메서드를 통해 반환값을 조작)
        when(user.getPoint()).thenReturn(BigDecimal.valueOf(50000));  // 잔액을 부족하게 설정

        // When & Then: 잔액이 부족한 상황에서 예외가 발생하는지 확인
        Pay command = new Pay(USER_ID, RESERVATION_ID);
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> {
            concertService.payReservation(command);
        });

        // 각 리포지토리가 예상대로 호출되었는지 확인
        verify(concertReservationRepository, times(1)).findById(RESERVATION_ID);
        verify(userRepository, times(1)).findByIdForUsePoint(USER_ID);

        // 좌석 조회는 안되어야 함.(잔액조회 실패 시 그 전에 Exception이 터져버리기 때문.
        verify(concertSeatRepository, times(0)).findById(CONCERTSEAT_ID);
    }


    @Test
    @Order(11)
    @DisplayName("[실패] 결제(payReservation 메서드) - 좌석 찾기 실패")
    void testPayReservation_Failure_SeatNotFound() {
        // Given: 예약과 유저가 정상적으로 조회됨
        when(concertReservationRepository.findById(RESERVATION_ID)).thenReturn(
            Optional.of(reservation));
        when(userRepository.findByIdForUsePoint(USER_ID)).thenReturn(Optional.of(user));

        // 유저의 잔액이 충분한 상태로 설정
        when(user.getPoint()).thenReturn(BigDecimal.valueOf(1000000));  // 충분한 잔액 설정

        // 좌석 조회가 실패하여 CONCERT_SEAT_NOT_FOUND 예외 발생
        when(concertSeatRepository.findById(CONCERTSEAT_ID)).thenReturn(Optional.empty());

        // When & Then: 좌석 조회 실패 시 예외 발생 확인
        Pay command = new Pay(USER_ID, RESERVATION_ID);
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> {
            concertService.payReservation(command);
        });

        // 예외가 발생할 때, 에러 코드가 CONCERT_SEAT_NOT_FOUND인지 확인
        assertEquals(ErrorCode.CONCERT_SEAT_NOT_FOUND, exception.getErrorCode());

        // 리포지토리 호출 횟수 검증
        verify(concertReservationRepository, times(1)).findById(
            RESERVATION_ID);  // 예약 조회가 한 번 호출되었는지 확인
        verify(userRepository, times(1)).findByIdForUsePoint(USER_ID);  // 유저 조회가 한 번 호출되었는지 확인
        verify(concertSeatRepository, times(1)).findById(CONCERTSEAT_ID);  // 좌석 조회가 한 번 호출되었는지 확인
    }

    @Test
    @Order(12)
    @DisplayName("[성공] 만료예약 스케줄러 (expireReservationProcess 메서드) ")
    void testExpireReservationProcess_Success() {
        // Given: 만료된 예약, Queue, 그리고 좌석이 정상적으로 조회됨
        when(concertReservationRepository.findExpiredReservations(
            eq(ReservationStatusType.RESERVED), any(LocalDateTime.class)))
            .thenReturn(List.of(reservation));

        when(queueRepository.findByUserId(reservation.getUserId())).thenReturn(Optional.of(queue));
        when(concertSeatRepository.findByIdAndStatus(reservation.getConcertSeatId(), SeatStatusType.RESERVED))
            .thenReturn(Optional.of(concertSeat));
        concertSeat.reserveSeat();

        // When: 만료된 예약 취소 프로세스를 실행
        concertService.expireReservationProcess();

        // Then: 예약이 취소되었고, 좌석 상태가 EMPTY로 변경되었는지 확인
        verify(concertReservationRepository, times(1)).findExpiredReservations(
            eq(ReservationStatusType.RESERVED), any(LocalDateTime.class));  // 만료된 예약 조회
        verify(queueRepository, times(1)).findByUserId(reservation.getUserId());  // Queue 조회
        verify(queueRepository, times(1)).delete(queue);  // Queue 삭제
        verify(concertSeatRepository, times(1)).findByIdAndStatus(reservation.getConcertSeatId(), SeatStatusType.RESERVED);  // 좌석 조회
        verify(concertSeat, times(1)).cancelSeatByReservation();  // 좌석 상태가 EMPTY로 변경되었는지 확인
        verify(reservation, times(1)).cancelReservation();  // 예약 상태가 CANCELED로 변경되었는지 확인
    }

    @Test
    @Order(13)
    @DisplayName("[실패] 만료예약 스케줄러 (expireReservationProcess 메서드) - Queue 조회 시 오류")
    void testExpireReservationProcess_Failure_QueueNotFound() {
        // Given: 만료된 예약은 조회되지만, Queue 조회 시 USER_NOT_FOUND 예외 발생
        when(concertReservationRepository.findExpiredReservations(
            eq(ReservationStatusType.RESERVED), any(LocalDateTime.class)))
            .thenReturn(List.of(reservation));

        when(queueRepository.findByUserId(reservation.getUserId())).thenReturn(Optional.empty());  // Queue 조회 실패

        // When & Then: Queue 조회 실패 시 예외 발생 확인
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> {
            concertService.expireReservationProcess();
        });

        // 예외 발생 시 에러 코드가 USER_NOT_FOUND인지 확인
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());

        // 리포지토리 호출 횟수 검증
        verify(concertReservationRepository, times(1)).findExpiredReservations(
            eq(ReservationStatusType.RESERVED), any(LocalDateTime.class));  // 만료된 예약 조회
        verify(queueRepository, times(1)).findByUserId(reservation.getUserId());  // Queue 조회
        verify(queueRepository, times(0)).delete(queue);  // Queue 삭제는 호출되지 않아야 함
    }


    @Test
    @Order(14)
    @DisplayName("[실패] 만료예약 스케줄러 (expireReservationProcess 메서드) - 예약 취소 시 오류")
    void testExpireReservationProcess_Failure_ReservationCancelError() {
        // Given

        // 만료된 예약 성공적으로 조회하도록.
        when(concertReservationRepository.findExpiredReservations(
            eq(ReservationStatusType.RESERVED), any(LocalDateTime.class)))
            .thenReturn(List.of(reservation));

        // 대기열 성공적으로 조회하도록.
        when(queueRepository.findByUserId(reservation.getUserId())).thenReturn(Optional.of(queue));

        // 예약 취소 시 터짐
        doThrow(new CustomGlobalException(ErrorCode.RESERVATION_NOT_RESERVED)).when(reservation).cancelReservation();  // 예약 취소에서 오류 발생

        // When & Then: 예약 취소 실패 시 예외 발생 확인
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> {
            concertService.expireReservationProcess();
        });

        // 예외 발생 시 에러 코드가 RESERVATION_NOT_RESERVED인지 확인
        assertEquals(ErrorCode.RESERVATION_NOT_RESERVED, exception.getErrorCode());

        // 리포지토리 호출 횟수 검증
        verify(concertReservationRepository, times(1)).findExpiredReservations(
            eq(ReservationStatusType.RESERVED), any(LocalDateTime.class));  // 만료된 예약 조회
        verify(queueRepository, times(1)).findByUserId(reservation.getUserId());  // Queue 조회
        verify(queueRepository, times(1)).delete(queue);  // Queue 삭제
        verify(reservation, times(1)).cancelReservation();  // 예약 취소 시도
        verify(concertSeatRepository, times(0)).findByIdAndStatus(concertSeat.getId(),concertSeat.getStatus());// 여기부턴 0번 수행되어야 함
        verify(concertSeat,times(0)).cancelSeatByReservation();
    }


    @Test
    @Order(15)
    @DisplayName("[실패] 만료예약 스케줄러 (expireReservationProcess 메서드) - 좌석 조회 시 오류")
    void testExpireReservationProcess_Failure_SeatNotFound() {
        // Given: Queue는 정상적으로 조회되지만, 좌석 조회에서 오류 발생
        when(concertReservationRepository.findExpiredReservations(
            eq(ReservationStatusType.RESERVED), any(LocalDateTime.class)))
            .thenReturn(List.of(reservation));

        when(queueRepository.findByUserId(reservation.getUserId())).thenReturn(Optional.of(queue));
        when(concertSeatRepository.findByIdAndStatus(reservation.getConcertSeatId(), SeatStatusType.RESERVED))
            .thenReturn(Optional.empty());  // 좌석 조회 실패

        // When & Then: 좌석 조회 실패 시 예외 발생 확인
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> {
            concertService.expireReservationProcess();
        });

        // 예외 발생 시 에러 코드가 CONCERT_SEAT_NOT_FOUND인지 확인
        assertEquals(ErrorCode.CONCERT_SEAT_NOT_FOUND, exception.getErrorCode());

        // 리포지토리 호출 횟수 검증
        verify(concertReservationRepository, times(1)).findExpiredReservations(
            eq(ReservationStatusType.RESERVED), any(LocalDateTime.class));  // 만료된 예약 조회
        verify(queueRepository, times(1)).findByUserId(reservation.getUserId());  // Queue 조회
        verify(queueRepository, times(1)).delete(queue);  // Queue 삭제
        verify(concertSeatRepository, times(1)).findByIdAndStatus(reservation.getConcertSeatId(), SeatStatusType.RESERVED);  // 좌석 조회
        verify(concertSeat,times(0)).cancelSeatByReservation();

    }


    @Test
    @Order(16)
    @DisplayName("[실패] 만료예약 스케줄러 (expireReservationProcess 메서드) - 좌석 상태 변경 시 오류")
    void testExpireReservationProcess_Failure_SeatCancelError() {
        // Given: Queue와 좌석은 정상적으로 조회되지만, 좌석 상태 변경에서 오류 발생
        when(concertReservationRepository.findExpiredReservations(
            eq(ReservationStatusType.RESERVED), any(LocalDateTime.class)))
            .thenReturn(List.of(reservation));

        when(queueRepository.findByUserId(reservation.getUserId())).thenReturn(Optional.of(queue));
        when(concertSeatRepository.findByIdAndStatus(reservation.getConcertSeatId(), SeatStatusType.RESERVED))
            .thenReturn(Optional.of(concertSeat));

        doThrow(new CustomGlobalException(ErrorCode.SEAT_NOT_RESERVED)).when(concertSeat).cancelSeatByReservation();  // 좌석 상태 변경 오류 발생

        // When & Then: 좌석 상태 변경 실패 시 예외 발생 확인
        CustomGlobalException exception = assertThrows(CustomGlobalException.class, () -> {
            concertService.expireReservationProcess();
        });

        // 예외 발생 시 에러 코드가 SEAT_CANCEL_ERROR인지 확인
        assertEquals(ErrorCode.SEAT_NOT_RESERVED, exception.getErrorCode());

        // 리포지토리 호출 횟수 검증
        verify(concertReservationRepository, times(1)).findExpiredReservations(
            eq(ReservationStatusType.RESERVED), any(LocalDateTime.class));  // 만료된 예약 조회
        verify(queueRepository, times(1)).findByUserId(reservation.getUserId());  // Queue 조회
        verify(queueRepository, times(1)).delete(queue);  // Queue 삭제
        verify(concertSeatRepository, times(1)).findByIdAndStatus(reservation.getConcertSeatId(), SeatStatusType.RESERVED);  // 좌석 조회
        verify(concertSeat, times(1)).cancelSeatByReservation();  // 좌석 상태 변경 시도
    }

}
