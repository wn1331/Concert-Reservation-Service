package hhplus.concertreservationservice.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class CustomGlobalException extends RuntimeException {

    private final ErrorCode errorCode;

    @Override
    public String getMessage(){
        return errorCode.getMessage();
    }

    public String getCode(){
        return errorCode.getCode();
    }

    public HttpStatus getHttpStatus(){
        return errorCode.getStatusCode();
    }
}
