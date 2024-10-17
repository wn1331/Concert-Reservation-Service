package hhplus.concertreservationservice.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class ApiControllerAdvice{
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity
            .badRequest()
            .body(new ErrorResponse("E00", e.getMessage()));
    }

    @ExceptionHandler(value = CustomGlobalException.class)
    public ResponseEntity<ErrorResponse> handleException(CustomGlobalException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(new ErrorResponse(e.getCode(), e.getMessage()));
    }
}