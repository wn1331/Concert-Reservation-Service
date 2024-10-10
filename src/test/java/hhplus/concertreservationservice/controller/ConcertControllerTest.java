package hhplus.concertreservationservice.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hhplus.concertreservationservice.interfaces.api.concert.controller.ConcertController;
import hhplus.concertreservationservice.interfaces.api.concert.dto.ConcertPay;
import hhplus.concertreservationservice.interfaces.api.concert.dto.ConcertReservation;
import hhplus.concertreservationservice.interfaces.api.concert.dto.ConcertSchedules;
import hhplus.concertreservationservice.interfaces.api.concert.dto.ConcertSchedules.Response.ConcertScheduleResponse;
import hhplus.concertreservationservice.interfaces.api.concert.dto.ConcertSeats;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ConcertController.class)
@DisplayName("ConcertController 테스트")
class ConcertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConcertController concertController;

    @Test
    @DisplayName("[성공] 콘서트 날짜 조회 API")
    void getConcertSchedulesTest() throws Exception {
        List<ConcertScheduleResponse> mockSchedules = Arrays.asList(
            new ConcertSchedules.Response.ConcertScheduleResponse(1L, LocalDate.of(2024, 10, 1), "매진됨"),
            new ConcertSchedules.Response.ConcertScheduleResponse(2L, LocalDate.of(2024, 10, 2), "예약가능")
        );
        ConcertSchedules.Response mockResponse = new ConcertSchedules.Response(mockSchedules);

        given(concertController.getConcertSchedules(1L, "gmrqordyfltk")).willReturn(ok(mockResponse));

        mockMvc.perform(get("/concert/1/schedules")
                .header("token", "gmrqordyfltk"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.schedules[0].scheduleId").value(1))
            .andExpect(jsonPath("$.schedules[0].status").value("매진됨"))
            .andExpect(jsonPath("$.schedules[1].scheduleId").value(2))
            .andExpect(jsonPath("$.schedules[1].status").value("예약가능"));
    }

    @Test
    @DisplayName("[성공] 콘서트 좌석 조회 API")
    void getConcertSeatsTest() throws Exception {
        List<ConcertSeats.Response.ConcertSeatResponse> mockSeats = Arrays.asList(
            new ConcertSeats.Response.ConcertSeatResponse(1L, 101, "예약가능"),
            new ConcertSeats.Response.ConcertSeatResponse(2L, 102, "예약가능")
        );
        ConcertSeats.Response mockResponse = new ConcertSeats.Response(mockSeats);

        given(concertController.getConcertSeats(1L, "gmrqordyfltk")).willReturn(ok(mockResponse));

        mockMvc.perform(get("/concert/schedule/1/seats")
                .header("token", "gmrqordyfltk"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.concertSeats[0].seatId").value(1))
            .andExpect(jsonPath("$.concertSeats[0].status").value("예약가능"))
            .andExpect(jsonPath("$.concertSeats[1].seatId").value(2))
            .andExpect(jsonPath("$.concertSeats[1].status").value("예약가능"));
    }

    @Test
    @DisplayName("[성공] 콘서트 좌석 예약 API")
    void reserveConcertTest() throws Exception {
        ConcertReservation.Response mockResponse = new ConcertReservation.Response(1L);

        String requestBody = """
            {
                "userId": 1,
                "concertScheduleId": 2,
                "concertSeatId": 101
            }
        """;
        ConcertReservation.Request mockRequest = new ConcertReservation.Request(1L, 2L, 101L);

        given(concertController.reserveConcert(mockRequest, "gmrqordyfltk")).willReturn(ok(mockResponse));

        mockMvc.perform(post("/concert/reservation")
                .header("token", "gmrqordyfltk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reservationId").value(1));
    }

    @Test
    @DisplayName("[성공] 콘서트 좌석 결제 API")
    void payConcertTest() throws Exception {
        ConcertPay.Response mockResponse = new ConcertPay.Response(1L, "결제성공");

        String requestBody = """
            {
                "userId": 1
            }
        """;
        ConcertPay.Request mockRequest = new ConcertPay.Request(1L);

        given(concertController.payConcert(1L, "gmrqordyfltk", mockRequest)).willReturn(ok(mockResponse));

        mockMvc.perform(post("/concert/reservation/1/pay")
                .header("token", "gmrqordyfltk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reservationId").value(1L))
            .andExpect(jsonPath("$.paymentStatus").value("결제성공"));
    }
}
