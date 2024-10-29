package hhplus.concertreservationservice.global.config;

import hhplus.concertreservationservice.global.listener.ReserveSeatRetryListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryListener;

@Configuration
public class RetryConfig {

    @Bean
    public RetryListener reserveSeatRetryListener(){
        return new ReserveSeatRetryListener();
    }

}
