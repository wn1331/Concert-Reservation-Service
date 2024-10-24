package hhplus.concertreservationservice.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    CONCERT_SEAT_NOT_FOUND("E01", HttpStatus.BAD_REQUEST, "해당 ID로 좌석을 찾을 수 없습니다."),
    ALREADY_RESERVED_SEAT("E02", HttpStatus.CONFLICT, "이미 예약된 좌석입니다."),
    ALREADY_SOLD_SEAT("E03", HttpStatus.CONFLICT, "이미 판매된 좌석입니다."),
    CONCERT_RESERVATION_NOT_FOUND("E04", HttpStatus.BAD_REQUEST, "예약을 찾을 수 없습니다."),
    USER_NOT_FOUND("E05", HttpStatus.BAD_REQUEST, "해당 ID로 유저를 찾을 수 없습니다."),
    NOT_ENOUGH_BALANCE("E06", HttpStatus.PAYMENT_REQUIRED, "잔액이 부족합니다."),
    QUEUE_NOT_FOUND("E07", HttpStatus.BAD_REQUEST, "대기열을 찾을 수 없습니다."),
    QUEUE_STILL_WAITING("E08", HttpStatus.CONFLICT, "대기열이 아직 처리 중입니다."),
    ALREADY_PAID_OR_CANCELLED("E09", HttpStatus.CONFLICT, "이미 결제된 상태이거나 예약 취소된 상태입니다."),
    RESERVATION_NOT_RESERVED("E10", HttpStatus.CONFLICT, "예약되어 있는 상태가 아닙니다."),
    SEAT_NOT_EMPTY("E11",HttpStatus.CONFLICT,"좌석이 비어있는 상태가 아닙니다." ),
    SEAT_NOT_RESERVED("E12",HttpStatus.CONFLICT,"좌석이 예약중인 상태가 아닙니다."),
    RESERVATION_TIMEOUT("E13",HttpStatus.GONE,"예약의 시간이 만료되었습니다." ),
    INVALID_QUEUE_TOKEN("E14",HttpStatus.BAD_REQUEST ,"대기열 토큰 헤더가 누락되었거나 올바르지 않습니다." ),



    BAD_REQUEST("E99",HttpStatus.NOT_FOUND ,"요청 오류입니다." );


    private final String code;
    private final HttpStatus statusCode;
    private final String message;

}

