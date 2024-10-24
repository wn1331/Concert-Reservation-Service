package hhplus.concertreservationservice.global.interceptor;

import hhplus.concertreservationservice.application.queue.dto.QueueCriteria.VerifyQueueForPay;
import hhplus.concertreservationservice.application.queue.facade.QueueFacade;
import hhplus.concertreservationservice.global.exception.CustomGlobalException;
import hhplus.concertreservationservice.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

@Component
@Slf4j
@RequiredArgsConstructor
public class QueueValidationForPayInterceptor implements HandlerInterceptor{

    private static final String QUEUE_TOKEN_HEADER = "X-Access-Token";

    private final QueueFacade queueFacade;

    @Override
    @SuppressWarnings("unchecked")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        log.info("QueueValidationForPayInterceptor preHandle processing...");

        String queueToken = request.getHeader(QUEUE_TOKEN_HEADER);

        // api로 도달 전에 검증해야 한다.
        if (queueToken == null) {
            throw new CustomGlobalException(ErrorCode.INVALID_QUEUE_TOKEN);
        }

        // 토큰 검증, reservationId를 가지고 올 수 있는 방법 찾아야 함.
        // PathVariable 값 추출(SupressWarnings로 unchecked 경고제거..!)
        Map<String, String> pathVariables = (Map<String, String>) request
            .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        // API에서 체크하지만, 인터셉터에서 검증이 필요하므로 여기에서 먼저 체크헤야 한다.
        if (pathVariables == null) {
            throw new CustomGlobalException(ErrorCode.BAD_REQUEST);
        }

        Long reservationId = Long.parseLong(pathVariables.get("reservationId"));

        queueFacade.queueValidationForPay(VerifyQueueForPay.builder()
            .queueToken(queueToken)
            .reservationId(reservationId)
            .build()
        );

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
        ModelAndView modelAndView){
        log.info("QueueValidationInterceptor postHandle processing...");

        // preHandle에서 token 검증했으니 추가 검증(null체크 등) 필요없다 판단.
        queueFacade.expireToken(request.getHeader(QUEUE_TOKEN_HEADER));

    }

}
