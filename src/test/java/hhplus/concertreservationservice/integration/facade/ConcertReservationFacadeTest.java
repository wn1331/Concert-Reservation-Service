package hhplus.concertreservationservice.integration.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import hhplus.concertreservationservice.application.concert.dto.ConcertCriteria;
import hhplus.concertreservationservice.application.concert.dto.ConcertResult;
import hhplus.concertreservationservice.application.concert.facade.ConcertReservationFacade;
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
import hhplus.concertreservationservice.domain.queue.entity.Queue;
import hhplus.concertreservationservice.domain.queue.entity.QueueStatusType;
import hhplus.concertreservationservice.domain.queue.repository.QueueRepository;
import hhplus.concertreservationservice.domain.user.entity.User;
import hhplus.concertreservationservice.domain.user.repository.UserRepository;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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

@SpringBootTest
@Transactional
@DisplayName("[통합 테스트] ConcertReservationFacade 테스트")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ConcertReservationFacadeTest {

    @Autowired
    private ConcertReservationFacade concertReservationFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ConcertScheduleRepository concertScheduleRepository;

    @Autowired
    private ConcertSeatRepository concertSeatRepository;

    @Autowired
    private ConcertReservationRepository concertReservationRepository;

    @Autowired
    private EntityManager entityManager;

    private User user;
    private Concert concert;
    private ConcertSchedule concertSchedule;
    private String queueToken = "exampleToken";


    @BeforeEach
    void setup() {
        // 테스트 유저 생성
        user = new User("테스트 유저", BigDecimal.valueOf(1000000));
        userRepository.save(user);

        // 대기열 생성
        Queue queue = new Queue(user.getId(), queueToken, QueueStatusType.PASS);
        queueRepository.save(queue);

        // 콘서트와 스케줄, 좌석 생성
        concert = new Concert("테스트 콘서트");
        concertRepository.save(concert);

        concertSchedule = new ConcertSchedule(concert.getId(), LocalDate.of(2024, 12, 1));
        concertScheduleRepository.save(concertSchedule);

        // 콘서트 좌석 생성
        ConcertSeat seat3 = new ConcertSeat(concertSchedule.getId(), "A3", BigDecimal.valueOf(150000),
            SeatStatusType.SOLD);
        List<ConcertSeat> seats = List.of(
            new ConcertSeat(concertSchedule.getId(), "A1", BigDecimal.valueOf(150000), SeatStatusType.EMPTY),
            new ConcertSeat(concertSchedule.getId(), "A2", BigDecimal.valueOf(150000), SeatStatusType.RESERVED),
            seat3
        );
        concertSeatRepository.saveAll(seats);

        ConcertReservation concertReservation = new ConcertReservation(user.getId(),
            302L, BigDecimal.valueOf(150000),
            ReservationStatusType.RESERVED);
        concertReservationRepository.save(concertReservation);

        ConcertReservation concertReservationExpireTest = new ConcertReservation(user.getId(),
            302L, BigDecimal.valueOf(150000),
            ReservationStatusType.RESERVED);
    }

    @Test
    @Order(1)
    @DisplayName("[성공] 콘서트 좌석 예약 테스트")
    void reserveSeat_success() {
        // Given
        ConcertCriteria.ReserveSeat criteria = ConcertCriteria.ReserveSeat.builder()
            .userId(user.getId())
            .concertSeatId(1L)  // A1 좌석 ID
            .build();

        // When
        ConcertResult.ReserveSeat result = concertReservationFacade.reserveSeat(criteria);

        // Then
        assertEquals(2L, result.reservationId());
    }

    @Test
    @Order(2)
    @DisplayName("[실패] 존재하지 않는 좌석 예약 시도")
    void reserveSeat_fail_whenSeatNotFound() {
        // Given
        ConcertCriteria.ReserveSeat criteria = ConcertCriteria.ReserveSeat.builder()
            .userId(1L)
            .concertSeatId(999L)  // 존재하지 않는 좌석 ID
            .build();

        // When & Then
        CustomGlobalException exception = assertThrows(CustomGlobalException.class,
            () -> concertReservationFacade.reserveSeat(criteria)
        );

        assertEquals(ErrorCode.CONCERT_SEAT_NOT_FOUND, exception.getErrorCode());
        assertEquals("해당 ID로 좌석을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("[실패] 이미 예약된 좌석을 예약 시도")
    void reserveSeat_fail_whenSeatAlreadyReserved() {
        // Given
        ConcertCriteria.ReserveSeat criteria = ConcertCriteria.ReserveSeat.builder()
            .userId(1L)
            .concertSeatId(302L)  // (1~300번 좌석은 local 어플리케이션 실행할 때 생성합니다. 301(empty)~302(reserved)번 좌석은 test 어플리케이션 실행 시 생성합니다)
            .build();

        // When & Then
        CustomGlobalException exception = assertThrows(CustomGlobalException.class,
            () -> concertReservationFacade.reserveSeat(criteria)
        );

        assertEquals(ErrorCode.ALREADY_RESERVED_SEAT, exception.getErrorCode());
        assertEquals("이미 예약된 좌석입니다.", exception.getMessage());
    }

    @Test
    @Order(4)
    @DisplayName("[실패] 이미 판매 완료된 좌석을 예약 시도")
    void reserveSeat_fail_whenSeatAlreadySold() {
        // Given: SOLD된 좌석 ID : 303
        ConcertCriteria.ReserveSeat criteria = ConcertCriteria.ReserveSeat.builder()
            .userId(1L)
            .concertSeatId(303L)  // A3 좌석 (SOLD 상태)
            .build();

        // When & Then
        CustomGlobalException exception = assertThrows(CustomGlobalException.class,
            () -> concertReservationFacade.reserveSeat(criteria)
        );

        assertEquals(ErrorCode.ALREADY_SOLD_SEAT, exception.getErrorCode());
        assertEquals("이미 판매된 좌석입니다.", exception.getMessage());
    }

    @Test
    @Order(5)
    @DisplayName("[성공][스케줄러] 만료된 예약을 정상적으로 취소하고 좌석 상태 변경")
    void expireReservationProcess_success() {
        // Given: 테스트 데이터를 만료된 예약 상태로 설정
        ConcertReservation expiredReservation = new ConcertReservation(
            user.getId(), 302L, BigDecimal.valueOf(150000), ReservationStatusType.RESERVED);
        concertReservationRepository.save(expiredReservation);

        // 기존 비즈니스 로직을 변경하지 않고 created_at을 10분 이전으로 생성하는 방법. -> entityManager 사용해서 update친다.
        String updateQuery = "UPDATE reservation SET created_at = :createdAt WHERE id = :reservationId";
        LocalDateTime expiredTime = LocalDateTime.now().minusMinutes(10);
        entityManager.createNativeQuery(updateQuery)
            .setParameter("createdAt", expiredTime)
            .setParameter("reservationId", expiredReservation.getId())
            .executeUpdate();


        // When: 만료 처리 실행
        concertReservationFacade.expireReservationProcess();

        // Then: 예약 상태가 CANCELED로 변경되었는지 확인
        ConcertReservation updatedReservation = concertReservationRepository.findById(expiredReservation.getId())
            .orElseThrow();
        assertEquals(ReservationStatusType.CANCELED, updatedReservation.getStatus());

        // 좌석 상태가 EMPTY로 변경되었는지 확인
        ConcertSeat updatedSeat = concertSeatRepository.findById(302L).orElseThrow();
        assertEquals(SeatStatusType.EMPTY, updatedSeat.getStatus());

        // 유저의 대기열이 삭제되었는지 확인
        assertFalse(queueRepository.findByUserId(user.getId()).isPresent());
    }


    @Test
    @Order(6)
    @DisplayName("[실패][스케줄러] 만료된 예약에 해당하는 좌석이 존재하지 않음")
    void expireReservationProcess_fail_whenSeatNotFound() {

        // 없는 좌석번호.
        ConcertReservation expiredReservation = new ConcertReservation(
            user.getId(), 999L, BigDecimal.valueOf(150000), ReservationStatusType.RESERVED);
        concertReservationRepository.save(expiredReservation);

        // 기존 비즈니스 로직을 변경하지 않고 created_at을 10분 이전으로 생성하는 방법. -> entityManager 사용해서 update친다.
        String updateQuery = "UPDATE reservation SET created_at = :createdAt WHERE id = :reservationId";
        LocalDateTime expiredTime = LocalDateTime.now().minusMinutes(10);
        entityManager.createNativeQuery(updateQuery)
            .setParameter("createdAt", expiredTime)
            .setParameter("reservationId", expiredReservation.getId())
            .executeUpdate();

        // When & Then: 만료 처리 중 예외 발생 검증
        CustomGlobalException exception = assertThrows(CustomGlobalException.class,
            () -> concertReservationFacade.expireReservationProcess());

        assertEquals(ErrorCode.CONCERT_SEAT_NOT_FOUND, exception.getErrorCode());
        assertEquals("해당 ID로 좌석을 찾을 수 없습니다.", exception.getMessage());
    }



}
