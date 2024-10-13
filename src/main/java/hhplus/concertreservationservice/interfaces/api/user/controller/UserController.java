package hhplus.concertreservationservice.interfaces.api.user.controller;

import static org.springframework.http.ResponseEntity.ok;

import hhplus.concertreservationservice.interfaces.api.user.dto.ChargeBalance;
import hhplus.concertreservationservice.interfaces.api.user.dto.CheckBalance;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
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

    // 유저 잔액 조회 API
    @GetMapping("/{userId}/balance")
    public ResponseEntity<CheckBalance.Response> checkBalance(
        @PathVariable(name = "userId") Long userId,
        @RequestHeader(name = "token") String token
    ) { // 헤더에서 token을 받음
        return ok(new CheckBalance.Response(userId, BigDecimal.valueOf(100000)));
    }

    // 유저 잔액 충전 API
    @PostMapping("/{userId}/balance")
    public ResponseEntity<ChargeBalance.Response> chargeBalance(
        @RequestHeader(name = "token") String token,
        @PathVariable(name = "userId") Long userId,
        @RequestBody @Valid ChargeBalance.Request request
    ) {
        return ok(new ChargeBalance.Response(userId, request.amount().add(BigDecimal.valueOf(10000))));
    }

}