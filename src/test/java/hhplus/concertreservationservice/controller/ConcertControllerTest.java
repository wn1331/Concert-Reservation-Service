package hhplus.concertreservationservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import hhplus.concertreservationservice.application.concert.dto.ConcertResult;
import hhplus.concertreservationservice.application.concert.facade.ConcertFacade;
import hhplus.concertreservationservice.application.concert.facade.ConcertPaymentFacade;
import hhplus.concertreservationservice.application.concert.facade.ConcertReservationFacade;
import hhplus.concertreservationservice.application.queue.facade.QueueFacade;
import hhplus.concertreservationservice.domain.concert.entity.ScheduleStatusType;
import hhplus.concertreservationservice.domain.concert.entity.SeatStatusType;
import hhplus.concertreservationservice.global.interceptor.QueueValidationInterceptor;
import hhplus.concertreservationservice.presentation.concert.controller.ConcertController;
import hhplus.concertreservationservice.presentation.concert.dto.ConcertRequest;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;


@WebMvcTest(ConcertController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("[단위 테스트] ConcertController")
@AutoConfigureWebMvc
class ConcertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConcertFacade concertFacade;

    @MockBean
    private ConcertPaymentFacade concertPaymentFacade;

    @MockBean
    private ConcertReservationFacade concertReservationFacade;

    @MockBean
    private QueueFacade queueFacade;


    @Test
    @Order(1)
    @DisplayName("[성공] 콘서트 스케줄 조회 성공")
    void getConcertSchedules_Success() throws Exception {
        Long concertId = 1L;
        String queueToken = "validToken";

        // Mocking the facade response
        when(concertFacade.getAvailableSchedules(any())).thenReturn(
            ConcertResult.AvailableSchedules.builder()
                .schedules(List.of(
                    ConcertResult.AvailableSchedules.ScheduleDetail.builder()
                        .id(1L)
                        .date(LocalDate.now())
                        .status(ScheduleStatusType.AVAILABLE)
                        .build()
                ))
                .build());

        // JSON String 직접 작성
        String expectedResponse = """
            {
                "schedules": [
                    {
                        "id": 1,
                        "date": "%s",
                        "status": "AVAILABLE"
                    }
                ]
            }
            """.formatted(LocalDate.now());  // date 부분에 동적인 날짜 값 처리

        mockMvc.perform(get("/concerts/{concertId}/schedules", concertId)
                .header("X-Access-Token", queueToken))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedResponse));  // JSON 응답 비교
    }


    @Test
    @Order(2)
    @DisplayName("[실패] 콘서트 스케줄 조회 실패 - 토큰 없음")
    void getConcertSchedules_Fail_NoToken() throws Exception {
        Long concertId = 1L;

        mockMvc.perform(get("/concerts/{concertId}/schedules", concertId))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Order(3)
    @DisplayName("[성공] 콘서트 좌석 조회 성공")
    void getConcertSeats_Success() throws Exception {
        Long concertScheduleId = 1L;
        String queueToken = "validToken";

        when(concertFacade.getAvailableSeats(any())).thenReturn(
            ConcertResult.AvailableSeats.builder()
                .seats(List.of(
                    ConcertResult.AvailableSeats.SeatDetail.builder()
                        .id(1L)
                        .seatNum("A1")
                        .status(SeatStatusType.EMPTY)
                        .build()
                ))
                .build());

        String expectedResponse = """
            {
                "seats": [
                    {
                        "id": 1,
                        "seatNum": "A1",
                        "status": "EMPTY"
                    }
                ]
            }
            """;

        mockMvc.perform(get("/concerts/schedules/{concertScheduleId}/seats", concertScheduleId)
                .header("X-Access-Token", queueToken))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedResponse));
    }

    @Test
    @Order(4)
    @DisplayName("[실패] 콘서트 좌석 조회 실패 - 토큰 없음")
    void getConcertSeats_Fail_NoToken() throws Exception {
        Long concertScheduleId = 1L;

        mockMvc.perform(get("/concerts/schedules/{concertScheduleId}/seats", concertScheduleId))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Order(5)
    @DisplayName("[성공] 콘서트 예약 성공")
    void reserveConcert_Success() throws Exception {
        String queueToken = "validToken";
        Long userId = 1L;
        Long concertSeatId = 1L;

        when(concertReservationFacade.reserveSeat(any())).thenReturn(
            ConcertResult.ReserveSeat.builder()
                .reservationId(1L)
                .build());

        String requestBody = """
            {
                "userId": %d,
                "concertSeatId": %d
            }
            """.formatted(userId, concertSeatId);

        String expectedResponse = """
            {
                "reservationId": 1
            }
            """;

        mockMvc.perform(post("/concerts/reservation")
                .header("X-Access-Token", queueToken)
                .contentType("application/json")
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedResponse));
    }

    @Test
    @Order(6)
    @DisplayName("[실패] 콘서트 예약 실패 - 유효성 검증 실패 (userId null)")
    void reserveConcert_Fail_InvalidRequest() throws Exception {
        String queueToken = "validToken";
        Long concertSeatId = 1L;

        String requestBody = """
            {
                "concertSeatId": %d
            }
            """.formatted(concertSeatId);

        mockMvc.perform(post("/concerts/reservation")
                .header("X-Access-Token", queueToken)
                .contentType("application/json")
                .content(requestBody))
            .andExpect(status().isBadRequest());
    }


    @Test
    @Order(7)
    @DisplayName("[성공] 콘서트 결제 성공")
    void payConcert_Success() throws Exception {
        String queueToken = "validToken";
        Long reservationId = 1L;
        Long userId = 1L;

        when(concertPaymentFacade.pay(any())).thenReturn(ConcertResult.Pay.builder()
            .paymentId(1L)
            .build());

        String requestBody = """
            {
                "userId": %d
            }
            """.formatted(userId);

        String expectedResponse = """
            {
                "paymentId": 1
            }
            """;

        mockMvc.perform(post("/concerts/reservations/{reservationId}/pay", reservationId)
                .header("X-Access-Token", queueToken)
                .contentType("application/json")
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(content().json(expectedResponse));
    }

    // 8. 콘서트 결제 실패 - 유효성 검증 실패 (userId null)
    @Test
    @Order(8)
    @DisplayName("[실패] 콘서트 결제 실패 - 유효성 검증 실패 (userId null)")
    void payConcert_Fail_InvalidRequest() throws Exception {
        String queueToken = "validToken";
        Long reservationId = 1L;

        String requestBody = """
            {}
            """;

        mockMvc.perform(post("/concerts/reservations/{reservationId}/pay", reservationId)
                .header("X-Access-Token", queueToken)
                .contentType("application/json")
                .content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Order(9)
    @DisplayName("[성공] 콘서트 목록 조회 성공")
    void getConcerts_Success() throws Exception {
        String queueToken = "validToken";

        when(concertFacade.getConcertList()).thenReturn(ConcertResult.GetConcertList.builder()
            .concertsResultList(List.of(
                ConcertResult.Concerts.builder()
                    .id(1L)
                    .title("Concert 1")
                    .build(),
                ConcertResult.Concerts.builder()
                    .id(2L)
                    .title("Concert 2")
                    .build()
            ))
            .build());

        String expectedResponse = """
        {
            "concertsList": [
                {
                    "id": 1,
                    "title": "Concert 1"
                },
                {
                    "id": 2,
                    "title": "Concert 2"
                }
            ]
        }
        """;

        mockMvc.perform(get("/concerts")
                .contentType("application/json")
                .header("X-Access-Token", queueToken)

            )
            .andExpect(status().isOk())
            .andExpect(content().json(expectedResponse));
    }


    @Test
    @Order(10)
    @DisplayName("[성공] 콘서트 생성 성공")
    void createConcert_Success() throws Exception {
        when(concertFacade.create(any())).thenReturn(ConcertResult.Create.builder()
            .id(1L)
            .build());

        String requestBody = """
            {
                "title": "New Concert",
                "dates": ["%s"],
                "seatAmount": 100,
                "price": 50.0
            }
            """.formatted(LocalDate.now());

        String expectedResponse = """
            {
                "id": 1
            }
            """;

        mockMvc.perform(post("/concerts/create")
                .contentType("application/json")
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(content().json(expectedResponse));
    }

    @Test
    @Order(11)
    @DisplayName("[실패] 콘서트 생성 실패 - 유효성 검증 실패 (빈 타이틀)")
    void createConcert_Fail_InvalidTitle() throws Exception {
        String requestBody = """
            {
                "title": "",
                "dates": ["%s"],
                "seatAmount": 100,
                "price": 50.0
            }
            """.formatted(LocalDate.now());

        mockMvc.perform(post("/concerts/create")
                .contentType("application/json")
                .content(requestBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("콘서트 타이틀은 빈값이거나 null일 수 없습니다."));
    }

    @Test
    @Order(12)
    @DisplayName("[실패] 콘서트 생성 실패 - 유효성 검증 실패 (좌석 수 누락)")
    void createConcert_Fail_MissingSeatAmount() throws Exception {
        String requestBody = """
            {
                "title": "New Concert",
                "dates": ["%s"],
                "price": 50.0
            }
            """.formatted(LocalDate.now());

        mockMvc.perform(post("/concerts/create")
                .contentType("application/json")
                .content(requestBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("콘서트 좌석 수는 null일 수 없습니다."));
    }

    @Test
    @Order(13)
    @DisplayName("[실패] 콘서트 생성 실패 - 유효성 검증 실패 (음수 가격)")
    void createConcert_Fail_InvalidPrice() throws Exception {
        String requestBody = """
            {
                "title": "New Concert",
                "dates": ["%s"],
                "seatAmount": 100,
                "price": -50.0
            }
            """.formatted(LocalDate.now());

        mockMvc.perform(post("/concerts/create")
                .contentType("application/json")
                .content(requestBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("양수인 실수를 입력해야 합니다."));
    }


}
