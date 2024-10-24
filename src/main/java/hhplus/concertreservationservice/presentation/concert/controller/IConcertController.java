package hhplus.concertreservationservice.presentation.concert.controller;

import hhplus.concertreservationservice.global.exception.ErrorResponse;
import hhplus.concertreservationservice.presentation.concert.dto.ConcertRequest;
import hhplus.concertreservationservice.presentation.concert.dto.ConcertResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

public interface IConcertController {

    @Operation(summary = "예약 가능 날짜 조회", description = "특정 콘서트의 예약 가능한 날짜를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "예약 가능 날짜 조회 성공",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ConcertResponse.AvailableSchedules.class))}),
        @ApiResponse(responseCode = "404", description = "대기열을 찾을 수 없음 (QUEUE_NOT_FOUND)",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "대기열이 아직 처리 중 (QUEUE_STILL_WAITING)",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/concerts/{concertId}/schedules")
    ResponseEntity<ConcertResponse.AvailableSchedules> getConcertSchedules(
        @PathVariable(name = "concertId") Long concertId
    );

    @Operation(summary = "콘서트 좌석 조회", description = "특정 콘서트 스케줄의 예약 가능한 좌석을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "예약 가능한 좌석 조회 성공",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ConcertResponse.AvailableSeats.class))}),
        @ApiResponse(responseCode = "404", description = "대기열을 찾을 수 없음 (QUEUE_NOT_FOUND)",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "대기열이 아직 처리 중 (QUEUE_STILL_WAITING)",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/schedules/{concertScheduleId}/seats")
    ResponseEntity<ConcertResponse.AvailableSeats> getConcertSeats(
        @PathVariable(name = "concertScheduleId") Long concertScheduleId
    );

    @Operation(summary = "콘서트 좌석 예약", description = "콘서트 좌석을 예약합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "좌석 예약 성공",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ConcertResponse.ReserveSeat.class))}),
        @ApiResponse(responseCode = "404", description = "대기열을 찾을 수 없음 (QUEUE_NOT_FOUND) 또는 좌석을 찾을 수 없음 (CONCERT_SEAT_NOT_FOUND)",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "대기열이 아직 처리 중 (QUEUE_STILL_WAITING), 이미 예약된 좌석 (ALREADY_RESERVED_SEAT), 또는 이미 판매된 좌석 (ALREADY_SOLD_SEAT)",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/reservation")
    ResponseEntity<ConcertResponse.ReserveSeat> reserveConcert(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "예약 요청 정보",
            content = @Content(mediaType = "application/json",
                schema = @Schema(example = "{\n"
                    + "  \"userId\": 1,\n"
                    + "  \"concertSeatId\": 11\n"
                    + "}")))
        @Valid @RequestBody ConcertRequest.ReserveSeat request
    );

    @Operation(summary = "콘서트 좌석 결제", description = "예약된 콘서트 좌석에 대한 결제를 진행합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "결제 성공",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ConcertResponse.Pay.class))}),
        @ApiResponse(responseCode = "404", description = "대기열 또는 예약 정보, 좌석 또는 사용자 정보를 찾을 수 없음",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "대기열 처리 중, 잔액 부족, 이미 판매된 좌석, 또는 타임아웃 등 결제 관련 예외",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/reservations/{reservationId}/pay")
    ResponseEntity<ConcertResponse.Pay> payConcert(
        @PathVariable(name = "reservationId") Long reservationId,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "결제 요청 정보",
            content = @Content(mediaType = "application/json",
                schema = @Schema(example = "{\n"
                    + "  \"userId\": 1\n"
                    + "}")))        @Valid @RequestBody ConcertRequest.Pay request
    );
}

