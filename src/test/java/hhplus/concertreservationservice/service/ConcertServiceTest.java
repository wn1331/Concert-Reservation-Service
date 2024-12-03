package hhplus.concertreservationservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hhplus.concertreservationservice.domain.concert.dto.ConcertCommand;
import hhplus.concertreservationservice.domain.concert.dto.ConcertInfo;
import hhplus.concertreservationservice.domain.concert.entity.ConcertSchedule;
import hhplus.concertreservationservice.domain.concert.entity.ConcertSeat;
import hhplus.concertreservationservice.domain.concert.entity.SeatStatusType;
import hhplus.concertreservationservice.domain.concert.repository.ConcertScheduleRepository;
import hhplus.concertreservationservice.domain.concert.repository.ConcertSeatRepository;
import hhplus.concertreservationservice.domain.concert.service.ConcertService;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
@DisplayName("[단위 테스트] ConcertService")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConcertServiceTest {

    @Mock
    private ConcertScheduleRepository concertScheduleRepository;

    @Mock
    private ConcertSeatRepository concertSeatRepository;

    @InjectMocks
    private ConcertService concertService;

    private ConcertSchedule concertSchedule;
    private ConcertSeat concertSeat;

    @BeforeEach
    void setUp() {
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

        // spy를 통해 객체를 감시하고 ID 값을 강제로 설정 가능
        concertSchedule = spy(concertSchedule);
        concertSeat = spy(concertSeat);
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
    @DisplayName("[성공] 좌석 상태 예약으로 변경 및 가격 반환 - EMPTY → RESERVED")
    void testChangeSeatStatusAndReturnPrice_Success() {
        // Given
        when(concertSeatRepository.findById(anyLong())).thenReturn(Optional.of(concertSeat));
        when(concertSeat.getStatus()).thenReturn(SeatStatusType.EMPTY);

        // reserveSeat() 호출 시 상태를 RESERVED로 변경
        doAnswer(invocation -> {
            when(concertSeat.getStatus()).thenReturn(SeatStatusType.RESERVED);
            return null;
        }).when(concertSeat).reserveSeat();

        // When
        BigDecimal price = concertService.changeSeatStatusAndReturnPrice(1L);

        // Then
        assertNotNull(price);
        assertEquals(concertSeat.getPrice(), price);
        assertEquals(SeatStatusType.RESERVED, concertSeat.getStatus());

        verify(concertSeatRepository, times(1)).findById(anyLong());
    }

    @Test
    @Order(4)
    @DisplayName("[실패] 좌석 상태 예약으로 변경 및 가격 반환 - 이미 예약된 좌석 (RESERVED)")
    void testChangeSeatStatusAndReturnPrice_AlreadyReserved() {
        // Given
        when(concertSeat.getStatus()).thenReturn(SeatStatusType.RESERVED);
        when(concertSeatRepository.findById(anyLong())).thenReturn(Optional.of(concertSeat));

        // When & Then
        CustomGlobalException exception = assertThrows(
            CustomGlobalException.class,
            () -> concertService.changeSeatStatusAndReturnPrice(1L)
        );

        assertEquals(ErrorCode.ALREADY_RESERVED_SEAT, exception.getErrorCode());
        verify(concertSeatRepository, times(1)).findById(anyLong());
    }

    @Test
    @Order(5)
    @DisplayName("[실패] 좌석 상태 예약으로 변경 및 가격 반환 - 이미 판매된 좌석 (SOLD)")
    void testChangeSeatStatusAndReturnPrice_AlreadySold() {
        // Given
        when(concertSeat.getStatus()).thenReturn(SeatStatusType.SOLD);
        when(concertSeatRepository.findById(anyLong())).thenReturn(Optional.of(concertSeat));

        // When & Then
        CustomGlobalException exception = assertThrows(
            CustomGlobalException.class,
            () -> concertService.changeSeatStatusAndReturnPrice(1L)
        );

        assertEquals(ErrorCode.ALREADY_SOLD_SEAT, exception.getErrorCode());
        verify(concertSeatRepository, times(1)).findById(anyLong());
    }

    @Test
    @Order(6)
    @DisplayName("[실패] 좌석 상태 예약으로 변경 및 가격 반환 - 좌석이 존재하지 않음")
    void testChangeSeatStatusAndReturnPrice_SeatNotFound() {
        // Given
        when(concertSeatRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        CustomGlobalException exception = assertThrows(
            CustomGlobalException.class,
            () -> concertService.changeSeatStatusAndReturnPrice(1L)
        );

        assertEquals(ErrorCode.CONCERT_SEAT_NOT_FOUND, exception.getErrorCode());
        verify(concertSeatRepository, times(1)).findById(anyLong());
    }

    @Test
    @Order(7)
    @DisplayName("[성공] 좌석 상태 결제됨으로 변경 - RESERVED → SOLD")
    void testChangeSeatStatusPaid_Success() {
        // Given
        when(concertSeat.getStatus()).thenReturn(SeatStatusType.RESERVED);
        when(concertSeatRepository.findById(anyLong())).thenReturn(Optional.of(concertSeat));

        // confirmSeatByPayment()가 호출될 때 상태를 SOLD로 변경
        doAnswer(invocation -> {
            when(concertSeat.getStatus()).thenReturn(SeatStatusType.SOLD);
            return null;
        }).when(concertSeat).confirmSeatByPayment();

        // When
        concertService.changeSeatStatusPaid(1L);

        // Then
        assertEquals(SeatStatusType.SOLD, concertSeat.getStatus());
        verify(concertSeatRepository, times(1)).findById(anyLong());
    }

    @Test
    @Order(8)
    @DisplayName("[실패] 좌석 상태 결제됨으로 변경 - 좌석 조회 실패")
    void testChangeSeatStatusPaid_SeatNotFound() {
        // Given
        when(concertSeatRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        CustomGlobalException exception = assertThrows(
            CustomGlobalException.class,
            () -> concertService.changeSeatStatusPaid(1L)
        );

        assertEquals(ErrorCode.CONCERT_SEAT_NOT_FOUND, exception.getErrorCode());
        verify(concertSeatRepository, times(1)).findById(anyLong());
    }


}
