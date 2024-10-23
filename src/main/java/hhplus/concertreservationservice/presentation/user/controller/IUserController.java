package hhplus.concertreservationservice.presentation.user.controller;

import hhplus.concertreservationservice.global.exception.ErrorResponse;
import hhplus.concertreservationservice.presentation.user.dto.UserRequest;
import hhplus.concertreservationservice.presentation.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

public interface IUserController {

    @Operation(summary = "유저 잔액 조회", description = "특정 유저의 잔액을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "잔액 조회 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = UserResponse.CheckBalance.class))),
        @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음 (USER_NOT_FOUND)",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "대기열이 아직 처리 중 (QUEUE_STILL_WAITING)",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{userId}/balance")
    ResponseEntity<UserResponse.CheckBalance> checkBalance(
        @PathVariable(name = "userId") Long userId,
        @RequestHeader(name = "X-Access-Token") String token
    );
    @Operation(summary = "유저 잔액 충전", description = "특정 유저의 잔액을 충전합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "잔액 충전 성공",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = UserResponse.ChargeBalance.class))),
        @ApiResponse(responseCode = "404", description = "유저를 찾을 수 없음 (USER_NOT_FOUND), 대기열을 찾을 수 없음 (QUEUE_NOT_FOUND)",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "대기열이 아직 처리 중 (QUEUE_STILL_WAITING)",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{userId}/balance")
    ResponseEntity<UserResponse.ChargeBalance> chargeBalance(
        @RequestHeader(name = "X-Access-Token") String token,
        @PathVariable(name = "userId") Long userId,

        @RequestBody(description = "잔액 충전 요청 정보", required = true,
            content = @Content(mediaType = "application/json",
                schema = @Schema(example = "{\n"
                    + "  \"amount\": 5000\n"
                    + "}")))
        @Valid UserRequest.ChargeBalance request
    );

}
