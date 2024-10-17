package hhplus.concertreservationservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import hhplus.concertreservationservice.application.user.dto.UserCriteria;
import hhplus.concertreservationservice.application.user.dto.UserResult;
import hhplus.concertreservationservice.application.user.facade.UserFacade;
import hhplus.concertreservationservice.presentation.user.controller.UserController;
import hhplus.concertreservationservice.presentation.user.dto.UserResponse;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@DisplayName("[단위 테스트] UserController")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserFacade userFacade;

    @Test
    @Order(1)
    @DisplayName("[상겅] 유저 잔액 조회 성공")
    void checkBalance_Success() throws Exception {
        // Given
        Long userId = 1L;
        String token = "validQueueToken";
        BigDecimal balance = new BigDecimal("100.0");

        UserResult.CheckBalance checkBalanceResult = new UserResult.CheckBalance(userId, balance);
        when(userFacade.checkBalance(any(UserCriteria.CheckBalance.class)))
            .thenReturn(checkBalanceResult);

        // When & Then
        mockMvc.perform(get("/users/{userId}/balance", userId)
                .header("queueToken", token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(userId))
            .andExpect(jsonPath("$.balance").value(balance));
    }

    @Test
    @Order(2)
    @DisplayName("[실패] 유저 잔액 조회 실패 - 토큰 없음")
    void checkBalance_MissingToken() throws Exception {
        // Given
        Long userId = 1L;

        // When & Then
        mockMvc.perform(get("/users/{userId}/balance", userId)
                // 토큰 없이 요청
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());  // HTTP 400을 기대
    }


    @Test
    @Order(3)
    @DisplayName("[성공] 유저 잔액 충전 성공")
    void chargeBalance_Success() throws Exception {
        // Given
        Long userId = 1L;
        String token = "validQueueToken";
        String validRequest = "{\"amount\": 100.0}";


        UserResult.ChargeBalance response = UserResult.ChargeBalance.builder()
            .userId(userId)
            .balance(BigDecimal.valueOf(100.0))
            .build();

        when(userFacade.chargeBalance(any())).thenReturn(response);

        // When & Then
        mockMvc.perform(put("/users/{userId}/balance", userId)
                .header("queueToken", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequest))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(userId))  // 응답의 userId 확인
            .andExpect(jsonPath("$.amount").value(100.00));  // 응답의 amount 확인
    }

    @Test
    @Order(4)
    @DisplayName("[실패] 유저 잔액 충전 실패 - 잘못된 금액")
    void chargeBalance_InvalidAmount() throws Exception {
        // Given
        Long userId = 1L;
        String token = "validQueueToken";

        // When & Then
        mockMvc.perform(put("/users/{userId}/balance", userId)
                .header("queueToken", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\": -10.00}"))  // 유효성 검증 실패
            .andExpect(status().isBadRequest());  // HTTP 400을 기대
    }

    // 토큰이 없을 경우 - 잔액 충전 실패
    @Test
    @Order(5)
    @DisplayName("[실패] 유저 잔액 충전 실패 - 토큰 없음")
    void chargeBalance_MissingToken() throws Exception {
        // Given
        Long userId = 1L;
        String validRequest = "{\"amount\": 100.00}";

        // When & Then
        mockMvc.perform(put("/users/{userId}/balance", userId)
                // 토큰 없이 요청
                .contentType(MediaType.APPLICATION_JSON)
                .content(validRequest))
            .andExpect(status().isBadRequest());  // HTTP 400을 기대
    }

    @Test
    @Order(6)
    @DisplayName("[실패] 유저 잔액 충전 실패 - 요청값이 null")
    void chargeBalance_NullRequestBody() throws Exception {
        // Given
        Long userId = 1L;
        String token = "validQueueToken";

        // When & Then
        mockMvc.perform(put("/users/{userId}/balance", userId)
                .header("queueToken", token)
                .contentType(MediaType.APPLICATION_JSON)
                // 요청 본문이 없는 경우(null인 경우)
                .content(""))
            .andExpect(status().isBadRequest());  // HTTP 400을 기대
    }



}
