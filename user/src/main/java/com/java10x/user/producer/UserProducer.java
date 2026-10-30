package com.java10x.user.producer;

import com.java10x.user.domain.UserModel;
import com.java10x.user.dto.EmailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserProducer {

    final RabbitTemplate rabbitTemplate;

    private final String routingKey = "email-queue";

    public void publishEvent(UserModel userModel){

        var emailDto = new EmailDto();
        emailDto.setUserId(userModel.getUserId());
        emailDto.setEmailTo(userModel.getEmail());
        emailDto.setEmailSubject("Welcome to My House");
        emailDto.setBody("Hello "+ userModel.getName() + "\n\nWelcome to MyHouse! We are excited to have on board.\n\nBest regards,\nJM Team");

        rabbitTemplate.convertAndSend("",routingKey, emailDto);
    }

}
