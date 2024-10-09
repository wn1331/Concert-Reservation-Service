package hhplus.concertreservationservice.presentation.user.controller;

import static org.springframework.http.ResponseEntity.ok;

import hhplus.concertreservationservice.presentation.user.dto.req.UserChargeBalanceRequest;
import hhplus.concertreservationservice.presentation.user.dto.res.UserChargeBalanceResponse;
import hhplus.concertreservationservice.presentation.user.dto.res.UserCheckBalanceResponse;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);


    @GetMapping("/balance/{userId}")
    public ResponseEntity<UserCheckBalanceResponse> checkBalance(
        @PathVariable(name = "userId") Long userId,
        @RequestHeader(name = "token") String token
    ) { // 헤더에서 token을 받음
        log.info("checkBalance userId: {}, token: {}", userId, token);
        return ok(new UserCheckBalanceResponse(userId, BigDecimal.valueOf(100000)));
    }

    @PostMapping("/balance/{userId}")
    public ResponseEntity<UserChargeBalanceResponse> chargeBalance(
        @PathVariable(name = "userId") Long userId, @RequestBody UserChargeBalanceRequest request) {
        log.info("chargeBalance userId: {}, amount: {}", userId, request.amount());
        return ok(new UserChargeBalanceResponse(userId, request.amount().add(BigDecimal.valueOf(10000))));
    }

}