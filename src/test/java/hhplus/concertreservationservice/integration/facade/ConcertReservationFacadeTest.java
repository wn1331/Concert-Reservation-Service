package hhplus.concertreservationservice.integration.facade;

import static org.junit.jupiter.api.Assertions.assertEquals;

import hhplus.concertreservationservice.application.concert.dto.ConcertCriteria;
import hhplus.concertreservationservice.application.concert.dto.ConcertResult;
import hhplus.concertreservationservice.application.concert.facade.ConcertFacade;
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

    private User user;
    private Concert concert;
    private ConcertSchedule concertSchedule;
    private String queueToken = "3b93aaaf-0ea8-49e4-be70-574a1813167s";


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
        List<ConcertSeat> seats = List.of(
            new ConcertSeat(concertSchedule.getId(), "A1", BigDecimal.valueOf(150000), SeatStatusType.EMPTY),
            new ConcertSeat(concertSchedule.getId(), "A2", BigDecimal.valueOf(150000), SeatStatusType.RESERVED)
        );
        concertSeatRepository.saveAll(seats);

        ConcertReservation concertReservation = new ConcertReservation(user.getId(),
            302L, BigDecimal.valueOf(150000),
            ReservationStatusType.RESERVED);
        concertReservationRepository.save(concertReservation);
    }

    @Test
    @Order(3)
    @DisplayName("[성공] 콘서트 좌석 예약 테스트")
    void reserveSeat_success() {
        // Given
        ConcertCriteria.ReserveSeat criteria = ConcertCriteria.ReserveSeat.builder()
            .userId(user.getId())
            .concertSeatId(1L)  // A1 좌석 ID
            .queueToken(queueToken)
            .build();

        // When
        ConcertResult.ReserveSeat result = concertReservationFacade.reserveSeat(criteria);

        // Then
        assertEquals(2L, result.reservationId());
    }

}
