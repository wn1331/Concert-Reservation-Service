package hhplus.concertreservationservice.global.filter;


import static java.util.Collections.enumeration;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
public class LoggingFilter implements Filter {

    private static final String NO_CONTENT = "No Content";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("logging filter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(
            (HttpServletRequest) request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(
            (HttpServletResponse) response);
        String path = wrappedRequest.getRequestURI();

        try {
            chain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            if (!path.startsWith("/actuator")) {

                loggingRequest(wrappedRequest);
                loggingResponse(wrappedResponse);

                // 응답 내용을 클라이언트로 전달해야 한다. 전달 이후에는 본문을 확인할 수 없다는 것이 특징.
                wrappedResponse.copyBodyToResponse();

                // 응답 헤더는 전달 이후에 확인 가능하다.
                loggingResponseHeaders(wrappedResponse);

            }


        }


    }

    @Override
    public void destroy() {
        log.info("logging filter destroy");
    }

    private static void loggingRequest(ContentCachingRequestWrapper request) {
        log.info("==== Request Start ====");
        log.info("Request URI : {}", request.getRequestURI());
        log.info("Request Method : {}", request.getMethod());
        log.info("Request Headers : {}", getHeaders(request));
        log.info("Request Parameters : {}", getParameters(request));
        log.info("Request Body : {}", getRequestBody(request));
        log.info("Request Content Type : {}", request.getContentType());
        log.info("==== Request End ====");
    }

    private static void loggingResponse(ContentCachingResponseWrapper response) {
        log.info("==== Response Start ====");
        log.info("Response Status : {}", response.getStatus());
        log.info("Response Body : {}", getResponseBody(response));
    }

    private static void loggingResponseHeaders(ContentCachingResponseWrapper response) {
        log.info("Response Headers : {}", getHeaders(response));
        log.info("==== Response End ====");
    }

    private static String getHeaders(HttpServletRequest request) {
        return getHeadersAsString(request.getHeaderNames(), request::getHeader);
    }

    private static String getHeaders(HttpServletResponse response) {
        return getHeadersAsString(
            enumeration(response.getHeaderNames()),
            name -> String.join(", ", response.getHeaders(name))
        );
    }

    private static String getHeadersAsString(Enumeration<String> headerNames,
        UnaryOperator<String> headerResolver) {
        StringBuilder headers = new StringBuilder();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headers.append(name).append(": ").append(headerResolver.apply(name)).append(", ");
        }
        // 마지막 콤마와 공백 제거
        if (headers.length() > 2) {
            headers.setLength(headers.length() - 2);
        }
        return headers.toString();
    }

    // 요청 파라미터 가져오기
    private static String getParameters(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.isEmpty()) {
            return NO_CONTENT;
        }
        return parameterMap.entrySet().stream()
            .map(entry -> entry.getKey() + "=" + String.join(",", entry.getValue()))
            .collect(Collectors.joining(", "));
    }

    // 요청 바디 가져오기
    private static String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length == 0) {
            return NO_CONTENT;
        }
        return new String(content, StandardCharsets.UTF_8);
    }

    // 응답 바디 가져오기
    private static String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();
        if (content.length == 0) {
            return NO_CONTENT;
        }
        return new String(content, StandardCharsets.UTF_8);
    }


}
