package hhplus.concertreservationservice.presentation.user.controller;

import static org.springframework.http.ResponseEntity.ok;

import hhplus.concertreservationservice.application.user.dto.UserCriteria;
import hhplus.concertreservationservice.application.user.facade.UserFacade;
import hhplus.concertreservationservice.presentation.user.dto.UserRequest;
import hhplus.concertreservationservice.presentation.user.dto.UserResponse;
import hhplus.concertreservationservice.presentation.user.dto.UserResponse.CheckBalance;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController implements IUserController{

    private final UserFacade userFacade;

    // 유저 잔액 조회 API
    @GetMapping("/{userId}/balance")
    public ResponseEntity<CheckBalance> checkBalance(
        @PathVariable(name = "userId") Long userId,
        @RequestHeader(name = "X-Access-Token") String token
    ) { // 헤더에서 token을 받음
        return ok(UserResponse.CheckBalance.fromResult(userFacade.checkBalance(
            UserCriteria.CheckBalance.builder()
                .userId(userId)
                .queueToken(token)
                .build()
        )));
    }

    // 유저 잔액 충전 API
    @PutMapping("/{userId}/balance")
    public ResponseEntity<UserResponse.ChargeBalance> chargeBalance(
        @RequestHeader(name = "X-Access-Token") String token,
        @PathVariable(name = "userId") Long userId,
        @RequestBody @Valid UserRequest.ChargeBalance request
    ){
        return ok(UserResponse.ChargeBalance.fromResult(userFacade.chargeBalance(
            UserCriteria.ChargeBalance.builder()
                .userId(userId)
                .amount(request.amount())
                .queueToken(token)
                .build()
        )));
    }

}