package hhplus.concertreservationservice.controller;

import hhplus.concertreservationservice.interfaces.api.user.controller.UserController;
import hhplus.concertreservationservice.interfaces.api.user.dto.CheckBalance;
import hhplus.concertreservationservice.interfaces.api.user.dto.ChargeBalance;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@DisplayName("UserController 테스트")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserController userController;

    @Test
    @DisplayName("[성공] 유저 잔액 조회 API 테스트")
    void checkBalanceTest() throws Exception {
        CheckBalance.Response mockResponse = new CheckBalance.Response(1L, BigDecimal.valueOf(100000));

        given(userController.checkBalance(1L, "gmrqordyfltk")).willReturn(ok(mockResponse));

        mockMvc.perform(get("/user/1/balance")
                .header("token", "gmrqordyfltk"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.amount").value(100000));
    }

    @Test
    @DisplayName("[성공] 유저 잔액 충전 API 테스트")
    void chargeBalanceTest() throws Exception {
        ChargeBalance.Response mockResponse = new ChargeBalance.Response(1L, BigDecimal.valueOf(150000));

        String requestBody = """
            {
                "amount": 50000
            }
        """;

        ChargeBalance.Request mockRequest = new ChargeBalance.Request(BigDecimal.valueOf(50000));

        given(userController.chargeBalance("gmrqordyfltk", 1L, mockRequest)).willReturn(ok(mockResponse));

        mockMvc.perform(post("/user/1/balance")
                .header("token", "gmrqordyfltk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.amount").value(150000));
    }
}

