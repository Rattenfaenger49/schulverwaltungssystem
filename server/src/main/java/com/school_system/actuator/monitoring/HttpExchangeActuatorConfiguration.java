package com.school_system.actuator.monitoring;


import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration

public class HttpExchangeActuatorConfiguration {
    HttpExchangeRepository httpExchangeRepository;
    @Bean
    public HttpExchangeRepository httpTraceRepository() {
        return httpExchangeRepository;
    }

}