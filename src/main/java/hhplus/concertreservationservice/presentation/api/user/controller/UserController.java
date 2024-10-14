package hhplus.concertreservationservice.presentation.api.user.controller;

import static org.springframework.http.ResponseEntity.ok;

import hhplus.concertreservationservice.presentation.api.user.dto.UserRequest;
import hhplus.concertreservationservice.presentation.api.user.dto.UserResponse;
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
@RequestMapping("/users")
public class UserController {

    // 유저 잔액 조회 API
    @GetMapping("/{userId}/balance")
    public ResponseEntity<UserResponse.CheckBalance> checkBalance(
        @PathVariable(name = "userId") Long userId,
        @RequestHeader(name = "token") String token
    ) { // 헤더에서 token을 받음
        return ok(null);
    }

    // 유저 잔액 충전 API
    @PostMapping("/{userId}/balance")
    public ResponseEntity<UserResponse.ChargeBalance> chargeBalance(
        @RequestHeader(name = "token") String token,
        @PathVariable(name = "userId") Long userId,
        @RequestBody @Valid UserRequest.ChargeBalance request
    ) {
        return ok(null);
    }

}