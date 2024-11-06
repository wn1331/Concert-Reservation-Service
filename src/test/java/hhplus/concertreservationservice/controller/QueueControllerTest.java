package hhplus.concertreservationservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import hhplus.concertreservationservice.application.queue.dto.QueueResult;
import hhplus.concertreservationservice.application.queue.facade.QueueFacade;
import hhplus.concertreservationservice.presentation.queue.controller.QueueController;
import hhplus.concertreservationservice.presentation.queue.dto.QueueRequest;
import hhplus.concertreservationservice.presentation.queue.dto.QueueResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

@WebMvcTest(QueueController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("[단위 테스트] QueueController")
class QueueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QueueFacade queueFacade;

    @Test
    @Order(1)
    @DisplayName("[성공] 대기열 생성 성공")
    void enqueue_Success() throws Exception {
        // Given
        String queueToken = "valid-token";

        // Mocking the response from queueFacade
        when(queueFacade.enqueue(any())).thenReturn(QueueResult.Enqueue.builder()
            .queueToken(queueToken)
            .build());

        // When & Then
        mockMvc.perform(post("/queues/enqueue")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\": 1}"))  // 유효한 요청 본문
            .andExpect(status().isCreated())  // 기대 상태: 201 Created
            .andExpect(header().string("X-Access-Token", queueToken));  // 헤더에 토큰 확인
    }

    @Test
    @Order(2)
    @DisplayName("[실패] 대기열 생성 실패 - 요청 본문 없음")
    void enqueue_Fail_NullRequestBody() throws Exception {
        // When & Then
        mockMvc.perform(post("/queues/enqueue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
            .andExpect(status().isBadRequest());  // 400 Bad Request 기대
    }

    @Test
    @Order(3)
    @DisplayName("[실패] 대기열 생성 실패 - 유저 ID 누락")
    void enqueue_Fail_MissingUserId() throws Exception {
        // When & Then
        mockMvc.perform(post("/queues/enqueue")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\": null}"))  // userId 누락
            .andExpect(status().isBadRequest()); // 400 Bad Request 기대
    }

    @Test
    @Order(4)
    @DisplayName("[성공] 대기열 순번 조회 성공")
    void getQueueStatus_Success() throws Exception {
        // Given
        String queueToken = "valid-token";
        Long order = 5L;

        // Mocking the response from queueFacade
        when(queueFacade.getQueueOrder(any())).thenReturn(QueueResult.Order.builder()
            .order(order)
            .build());

        // When & Then
        mockMvc.perform(get("/queues/order")
                .header("X-Access-Token", queueToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.order").value(order));  // 순번 확인
    }

    @Test
    @Order(5)
    @DisplayName("[실패] 대기열 순번 조회 실패 - 토큰 누락")
    void getQueueStatus_Fail_MissingToken() throws Exception {
        // When & Then
        mockMvc.perform(get("/queues/order"))
            .andExpect(status().isBadRequest());  // 400 Bad Request 기대
    }
}
