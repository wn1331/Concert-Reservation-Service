package hhplus.concertreservationservice.global.exception;

public record ErrorResponse(
    String code,
    String message
) {

}
