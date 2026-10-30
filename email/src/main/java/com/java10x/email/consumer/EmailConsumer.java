package com.java10x.email.consumer;

import com.java10x.email.domain.EmailModel;
import com.java10x.email.dto.EmailDto;
import com.java10x.email.service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class EmailConsumer {


    final EmailService emailService;

    public EmailConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = "email-queue")
    public void listenEmailQueue(@Payload EmailDto emailDto){
        var email = new EmailModel();
        BeanUtils.copyProperties(emailDto,email);
        emailService.sendAndSaveEmail(email);

    }

}
