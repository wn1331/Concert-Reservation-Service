package hhplus.concertreservationservice.global.interceptor;

import hhplus.concertreservationservice.application.queue.dto.QueueCriteria;
import hhplus.concertreservationservice.application.queue.facade.QueueFacade;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@Slf4j
@RequiredArgsConstructor
public class QueueValidationInterceptor implements HandlerInterceptor {

    private static final String QUEUE_TOKEN_HEADER = "queueToken";

    private final QueueFacade queueFacade;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        String queueToken = request.getHeader(QUEUE_TOKEN_HEADER);

        log.info("QueueValidationInterceptor pre 실행");

        // api로 도달 전에 검증해야 한다.
        if (queueToken == null) {
            throw new CustomGlobalException(ErrorCode.INVALID_QUEUE_TOKEN);
        }

        // 토큰 검증
        queueFacade.queueValidation(QueueCriteria.VerifyQueue.builder()
                .queueToken(queueToken)
            .build()
        );

        return true;
    }

}
