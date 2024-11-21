package hhplus.concertreservationservice.integration.facade;

import hhplus.concertreservationservice.application.concert.dto.ConcertCriteria;
import hhplus.concertreservationservice.application.concert.dto.ConcertResult;
import hhplus.concertreservationservice.application.concert.facade.ConcertFacade;
import hhplus.concertreservationservice.domain.concert.entity.SeatStatusType;

import hhplus.concertreservationservice.domain.user.entity.User;
import hhplus.concertreservationservice.domain.user.repository.UserRepository;
import hhplus.concertreservationservice.domain.concert.entity.Concert;
import hhplus.concertreservationservice.domain.concert.entity.ConcertSchedule;
import hhplus.concertreservationservice.domain.concert.entity.ConcertSeat;
import hhplus.concertreservationservice.domain.concert.repository.ConcertRepository;
import hhplus.concertreservationservice.domain.concert.repository.ConcertScheduleRepository;
import hhplus.concertreservationservice.domain.concert.repository.ConcertSeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("[통합 테스트] ConcertFacade 테스트")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ConcertFacadeTest {

    @Autowired
    private ConcertFacade concertFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ConcertScheduleRepository concertScheduleRepository;

    @Autowired
    private ConcertSeatRepository concertSeatRepository;

    private User user;
    private Concert concert;
    private ConcertSchedule concertSchedule;

    @BeforeEach
    void setup() {
        // 테스트 유저 생성
        user = new User("테스트 유저", BigDecimal.valueOf(1000000));
        userRepository.save(user);

        // 대기열 생성

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

    }

    @Test
    @Order(1)
    @DisplayName("[성공] 콘서트 스케줄 조회 테스트. 개수는 3개가 조회되어야 한다.")
    void getAvailableSchedules_success() {
        // Given
        ConcertCriteria.GetAvailableSchedules criteria = ConcertCriteria.GetAvailableSchedules.builder()
            .concertId(concert.getId())
            .build();

        // When
        ConcertResult.AvailableSchedules result = concertFacade.getAvailableSchedules(criteria);

        // Then
        // 기존에 2개(data.sql) 있음 + setup에서 1개 만듦
        assertEquals(3, result.schedules().size());
    }

    @Test
    @Order(2)
    @DisplayName("[성공] 콘서트 좌석 조회 테스트")
    void getAvailableSeats_success() {
        // Given
        ConcertCriteria.GetAvailableSeats criteria = ConcertCriteria.GetAvailableSeats.builder()
            .concertScheduleId(concertSchedule.getId())
            .build();

        // When
        ConcertResult.AvailableSeats result = concertFacade.getAvailableSeats(criteria);

        // Then
        assertEquals(1, result.seats().size());  // 2개의 좌석이 있어야 함
        assertEquals("A1", result.seats().get(0).seatNum());
    }

    @Test
    @Order(3)
    @DisplayName("[성공] 콘서트 목록 조회 테스트")
    void getConcertList_success() {

        // When
        ConcertResult.GetConcertList result = concertFacade.getConcertList();

        // Then
        assertEquals(1, result.concertsResultList().size());
        assertEquals(concert.getId(), result.concertsResultList().get(0).id());
        assertEquals(concert.getTitle(), result.concertsResultList().get(0).title());
    }

    @Test
    @Order(4)
    @DisplayName("[성공] 콘서트 생성 테스트")
    void createConcert_success() {
        // Given
        ConcertCriteria.Create criteria = ConcertCriteria.Create.builder()
            .title("새로운 콘서트")
            .dates(List.of(LocalDate.of(2024, 12, 15)))
            .seatAmount(100)
            .price(BigDecimal.valueOf(200000))
            .build();

        // When
        ConcertResult.Create result = concertFacade.create(criteria);

        // Then
        Concert createdConcert = concertRepository.findById(result.id()).orElse(null);
        assertEquals(result.id(), createdConcert.getId());
        assertEquals("새로운 콘서트", createdConcert.getTitle());
    }


}
