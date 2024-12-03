package hhplus.concertreservationservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hhplus.concertreservationservice.domain.concert.dto.ConcertCommand.ReserveSeat;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo.ReservationStatusInfo;
import hhplus.concertreservationservice.domain.concert.entity.ConcertPayment;
import hhplus.concertreservationservice.domain.concert.entity.ConcertReservation;
import hhplus.concertreservationservice.domain.concert.entity.ConcertSeat;
import hhplus.concertreservationservice.domain.concert.entity.PaymentStatusType;
import hhplus.concertreservationservice.domain.concert.entity.ReservationStatusType;
import hhplus.concertreservationservice.domain.concert.entity.SeatStatusType;
import hhplus.concertreservationservice.domain.concert.repository.ConcertReservationRepository;
import hhplus.concertreservationservice.domain.concert.repository.ConcertSeatRepository;
import hhplus.concertreservationservice.domain.concert.service.ConcertReservationService;
import hhplus.concertreservationservice.domain.queue.repository.QueueRepository;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("[단위 테스트] ConcertReservationService")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConcertReservationServiceTest {

    @Mock
    private QueueRepository queueRepository;

    @Mock
    private ConcertReservationRepository concertReservationRepository;

    @Mock
    private ConcertSeatRepository concertSeatRepository;

    @InjectMocks
    private ConcertReservationService concertReservationService;

    private ConcertPayment payment;
    private ConcertReservation reservation;
    private ConcertSeat concertSeat;

    private final Long RESERVATION_ID = 1L;
    private final Long CONCERTSEAT_ID = 2L;
    private final Long USER_ID = 1L;
    private final BigDecimal SEAT_PRICE = BigDecimal.valueOf(150000);
    private final String QUEUE_TOKEN = UUID.randomUUID().toString();


    @BeforeEach
    void setUp() {


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



        // spy를 통해 객체를 감시하고 ID 값을 강제로 설정 가능

        payment = spy(payment);
        reservation = spy(reservation);
        concertSeat = spy(concertSeat);

    }

    @Test
    @Order(1)
    @DisplayName("[성공] 좌석예약(reserveSeat 메서드)")
    void reserveSeat_shouldCreateNewReservation() {
        // Given
        ReserveSeat command = new ReserveSeat(USER_ID, CONCERTSEAT_ID);
        ConcertReservation savedReservation = ConcertReservation.builder()
            .userId(USER_ID)
            .concertSeatId(CONCERTSEAT_ID)
            .price(SEAT_PRICE)
            .status(ReservationStatusType.RESERVED)
            .build();

        when(concertSeatRepository.findById(CONCERTSEAT_ID)).thenReturn(Optional.of(concertSeat));
        when(concertReservationRepository.save(any(ConcertReservation.class)))
            .thenReturn(savedReservation);

        // When
        ConcertInfo.ReserveSeat result = concertReservationService.reserveSeat(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.reservationId()).isEqualTo(savedReservation.getId());
        verify(concertReservationRepository, times(1)).save(any(ConcertReservation.class));
    }

    @Test
    @Order(2)
    @DisplayName("[실패] 좌석예약(reserveSeat 메서드) - 이미 판매된 좌석")
    void reserveSeat_shouldThrowExceptionWhenSeatAlreadySold() {
        // Given
        ReserveSeat command = new ReserveSeat(USER_ID, CONCERTSEAT_ID);

        when(concertSeatRepository.findById(CONCERTSEAT_ID)).thenReturn(Optional.of(concertSeat));

        // 해당 좌석이 이미 예약된 상태로 리포지토리에서 조회되도록 설정
        when(concertReservationRepository.save(any(ConcertReservation.class)))
            .thenThrow(new CustomGlobalException(ErrorCode.ALREADY_RESERVED_SEAT));

        // When & Then
        assertThatThrownBy(() -> concertReservationService.reserveSeat(command))
            .isInstanceOf(CustomGlobalException.class)
            .hasMessage(ErrorCode.ALREADY_RESERVED_SEAT.getMessage());

        verify(concertReservationRepository, times(1)).save(any(ConcertReservation.class));
    }


    @Test
    @Order(3)
    @DisplayName("[성공] 예약상태 변경 (changeReservationStatusPaid 메서드): 예약 상태를 PAY_SUCCEED로 변경한다.")
    void changeReservationStatusPaid_shouldChangeStatusToPaid() {
        // Given
        ConcertReservation reservation = ConcertReservation.builder()
            .userId(USER_ID)
            .concertSeatId(CONCERTSEAT_ID)
            .price(SEAT_PRICE)
            .status(ReservationStatusType.RESERVED)
            .build();

        when(concertReservationRepository.findById(RESERVATION_ID))
            .thenReturn(Optional.of(reservation));

        // When
        ReservationStatusInfo result = concertReservationService.changeReservationStatusPaid(
            RESERVATION_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.reservationId()).isEqualTo(reservation.getId());
        assertThat(result.concertSeatId()).isEqualTo(reservation.getConcertSeatId());
        assertThat(result.price()).isEqualTo(reservation.getPrice());
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatusType.PAY_SUCCEED);

        verify(concertReservationRepository, times(1)).findById(RESERVATION_ID);
    }


    @Test
    @Order(4)
    @DisplayName("[실패] 예약상태 변경 (changeReservationStatusPaid 메서드): 예약이 없을 경우 예외를 발생시킨다.")
    void changeReservationStatusPaid_shouldThrowExceptionWhenReservationNotFound() {
        // Given
        when(concertReservationRepository.findById(RESERVATION_ID))
            .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(
            () -> concertReservationService.changeReservationStatusPaid(RESERVATION_ID))
            .isInstanceOf(CustomGlobalException.class)
            .hasMessage(ErrorCode.CONCERT_RESERVATION_NOT_FOUND.getMessage());

        verify(concertReservationRepository, times(1)).findById(RESERVATION_ID);
    }

    @Test
    @Order(5)
    @DisplayName("[성공] 만료예약 스케줄러 (expireReservationProcess 메서드) ")
    void testExpireReservationProcess_Success() {
        // Given
        ConcertReservation expiredReservation = ConcertReservation.builder()
            .userId(USER_ID)
            .concertSeatId(CONCERTSEAT_ID)
            .price(SEAT_PRICE)
            .status(ReservationStatusType.RESERVED)
            .build();

        // findExpiredReservations에 대한 스텁 설정
        doReturn(List.of(expiredReservation))
            .when(concertReservationRepository)
            .findExpiredReservations(eq(ReservationStatusType.RESERVED), any(LocalDateTime.class));

        ConcertSeat reservedSeat = ConcertSeat.builder()
            .concertScheduleId(1L)
            .seatNum("E01")
            .price(SEAT_PRICE)
            .status(SeatStatusType.RESERVED)
            .build();

        when(concertSeatRepository.findByIdAndStatus(CONCERTSEAT_ID, SeatStatusType.RESERVED))
            .thenReturn(Optional.of(reservedSeat));

        // When
        concertReservationService.expireReservationProcess();

        // Then
        assertThat(expiredReservation.getStatus()).isEqualTo(ReservationStatusType.CANCELED);
        assertThat(reservedSeat.getStatus()).isEqualTo(SeatStatusType.EMPTY);

        verify(concertReservationRepository, times(1)).findExpiredReservations(
            eq(ReservationStatusType.RESERVED), any(LocalDateTime.class));
        verify(concertSeatRepository, times(1)).findByIdAndStatus(CONCERTSEAT_ID, SeatStatusType.RESERVED);
    }


    @Test
    @Order(6)
    @DisplayName("[실패] 만료예약 스케줄러 (expireReservationProcess 메서드) - 예약 취소 시 오류")
    void testExpireReservationProcess_Failure_ReservationCancelError() {
        // Given
        ConcertReservation expiredReservation = Mockito.mock(ConcertReservation.class);

        // findExpiredReservations에 대한 스텁 설정
        doReturn(List.of(expiredReservation))
            .when(concertReservationRepository)
            .findExpiredReservations(eq(ReservationStatusType.RESERVED), any(LocalDateTime.class));

        // 예약 취소 시 예외 발생
        doThrow(new CustomGlobalException(ErrorCode.RESERVATION_NOT_RESERVED))
            .when(expiredReservation).cancelReservation();

        // When & Then
        assertThatThrownBy(() -> concertReservationService.expireReservationProcess())
            .isInstanceOf(CustomGlobalException.class)
            .hasMessage(ErrorCode.RESERVATION_NOT_RESERVED.getMessage());

        verify(concertReservationRepository, times(1)).findExpiredReservations(
            eq(ReservationStatusType.RESERVED), any(LocalDateTime.class));
    }

    @Test
    @Order(7)
    @DisplayName("[실패] 만료예약 스케줄러 (expireReservationProcess 메서드) - 좌석 조회 시 오류")
    void testExpireReservationProcess_Failure_SeatNotFound() {
        // Given
        ConcertReservation expiredReservation = ConcertReservation.builder()
            .userId(USER_ID)
            .concertSeatId(CONCERTSEAT_ID)
            .price(SEAT_PRICE)
            .status(ReservationStatusType.RESERVED)
            .build();

        // findExpiredReservations에 대한 유연한 매처 설정
        when(concertReservationRepository.findExpiredReservations(
            eq(ReservationStatusType.RESERVED), any(LocalDateTime.class)))
            .thenReturn(List.of(expiredReservation));

        // 좌석이 조회되지 않아 예외 발생
        when(concertSeatRepository.findByIdAndStatus(CONCERTSEAT_ID, SeatStatusType.RESERVED))
            .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> concertReservationService.expireReservationProcess())
            .isInstanceOf(CustomGlobalException.class)
            .hasMessage(ErrorCode.CONCERT_SEAT_NOT_FOUND.getMessage());

        verify(concertReservationRepository, times(1)).findExpiredReservations(
            eq(ReservationStatusType.RESERVED), any(LocalDateTime.class));
        verify(concertSeatRepository, times(1)).findByIdAndStatus(CONCERTSEAT_ID, SeatStatusType.RESERVED);
    }

    @Test
    @Order(8)
    @DisplayName("[실패] 만료예약 스케줄러 (expireReservationProcess 메서드) - 좌석 상태 변경 시 오류")
    void testExpireReservationProcess_Failure_SeatCancelError() {
        // Given
        ConcertReservation expiredReservation = ConcertReservation.builder()
            .userId(USER_ID)
            .concertSeatId(CONCERTSEAT_ID)
            .price(SEAT_PRICE)
            .status(ReservationStatusType.RESERVED)
            .build();

        ConcertSeat reservedSeat = Mockito.mock(ConcertSeat.class);

        // findExpiredReservations에 대한 유연한 매처 설정
        when(concertReservationRepository.findExpiredReservations(
            eq(ReservationStatusType.RESERVED), any(LocalDateTime.class)))
            .thenReturn(List.of(expiredReservation));

        when(concertSeatRepository.findByIdAndStatus(CONCERTSEAT_ID, SeatStatusType.RESERVED))
            .thenReturn(Optional.of(reservedSeat));

        // 좌석 상태 변경 시 예외 발생
        doThrow(new CustomGlobalException(ErrorCode.SEAT_NOT_RESERVED))
            .when(reservedSeat).cancelSeatByReservation();

        // When & Then
        assertThatThrownBy(() -> concertReservationService.expireReservationProcess())
            .isInstanceOf(CustomGlobalException.class)
            .hasMessage(ErrorCode.SEAT_NOT_RESERVED.getMessage());

        verify(concertReservationRepository, times(1)).findExpiredReservations(
            eq(ReservationStatusType.RESERVED), any(LocalDateTime.class));
        verify(concertSeatRepository, times(1)).findByIdAndStatus(CONCERTSEAT_ID, SeatStatusType.RESERVED);
    }



}
