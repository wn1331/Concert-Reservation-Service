package hhplus.concertreservationservice.global.config;

import hhplus.concertreservationservice.global.filter.LoggingFilter;
import hhplus.concertreservationservice.global.interceptor.QueueValidationForPayInterceptor;
import hhplus.concertreservationservice.global.interceptor.QueueValidationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final QueueValidationInterceptor queueValidationInterceptor;
    private final QueueValidationForPayInterceptor queueValidationForPayInterceptor;

    @Bean
    public FilterRegistrationBean<LoggingFilter> loggingFilter() {
        FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoggingFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(queueValidationInterceptor)
            .addPathPatterns("/**")
            .excludePathPatterns("/concerts/reservations/*/pay")
            .excludePathPatterns("/concerts/create")
            .excludePathPatterns("/swagger-ui/**")
            .excludePathPatterns("/v3/api-docs/**")
            .excludePathPatterns("/queues/**");
        registry.addInterceptor(queueValidationForPayInterceptor)
            .addPathPatterns("/concerts/reservations/*/pay");
    }

}
