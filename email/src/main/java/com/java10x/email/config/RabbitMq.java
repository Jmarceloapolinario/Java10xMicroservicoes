package com.java10x.email.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RabbitMq {

    private final String queueName = "email-queue";

    @Bean
    public Queue queue(){
        return new Queue(queueName,true);
    }
    @Bean
    public JacksonJsonMessageConverter messageConverter(JsonMapper objectMapper) {
        return new JacksonJsonMessageConverter(objectMapper);
    }

}
