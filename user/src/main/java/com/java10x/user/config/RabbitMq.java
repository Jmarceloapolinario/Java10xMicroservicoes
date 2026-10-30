package com.java10x.user.config;

import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RabbitMq {


    @Bean
    public JacksonJsonMessageConverter messageConverter(JsonMapper objectMapper) {
        return new JacksonJsonMessageConverter(objectMapper);
    }


}
