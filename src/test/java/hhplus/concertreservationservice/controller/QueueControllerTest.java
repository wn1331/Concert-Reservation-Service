package hhplus.concertreservationservice.controller;

import hhplus.concertreservationservice.interfaces.api.queue.controller.QueueController;
import hhplus.concertreservationservice.interfaces.api.queue.dto.Enqueue;
import hhplus.concertreservationservice.interfaces.api.queue.dto.QueuePoll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QueueController.class)
@DisplayName("QueueController 테스트")
class QueueControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QueueController queueController;

    @Test
    @DisplayName("[성공] 대기열 토큰 생성 요청 API 테스트")
    void enqueueTest() throws Exception {
        Enqueue.Response mockResponse = new Enqueue.Response("gmrqordyfltk");

        given(queueController.enqueue(1L)).willReturn(ok(mockResponse));

        mockMvc.perform(post("/queue/enqueue/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("gmrqordyfltk"));
    }

    @Test
    @DisplayName("[성공] 대기열 토큰 순번 조회 API 테스트")
    void pollQueueTest() throws Exception {
        QueuePoll.Response mockResponse = new QueuePoll.Response(1L, "gmrqordyfltk", 1L);

        given(queueController.poll("gmrqordyfltk")).willReturn(ok(mockResponse));

        mockMvc.perform(get("/queue/poll")
                .header("token", "gmrqordyfltk"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.order").value(1));
    }
}
