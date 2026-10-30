package com.java10x.email.service;

import com.java10x.email.domain.EmailModel;
import com.java10x.email.enums.EmailStatus;
import com.java10x.email.repository.EmailRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Log4j2
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailRepository emailRepository;

    @Value("${EMAIL_USERNAME}")
    private String emailFrom;

    public EmailService(JavaMailSender mailSender, EmailRepository emailRepository) {
        this.mailSender = mailSender;
        this.emailRepository = emailRepository;
    }

    @Transactional
    public EmailModel sendAndSaveEmail(EmailModel emailModel) {
        emailModel.setEmailFrom(emailFrom);
        emailModel.setSendDateEmail(LocalDateTime.now());
        emailModel.setEmailStatus(EmailStatus.PENDING);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailFrom);
            message.setTo(emailModel.getEmailTo());
            message.setSubject(emailModel.getEmailSubject());
            message.setText(emailModel.getBody());

            mailSender.send(message);

            emailModel.setEmailStatus(EmailStatus.SENT);
            log.info("Email enviado com sucesso para: {}", emailModel.getEmailTo());

        } catch (Exception e) {
            emailModel.setEmailStatus(EmailStatus.FAILED);
            log.error("Erro ao enviar email para: {} - Mensagem de erro: {}", emailModel.getEmailTo(), e.getMessage());
            throw new RuntimeException("Erro ao processar o envio de email: " + e.getMessage(), e);
        }

        return emailRepository.save(emailModel);
    }

    public EmailModel getEmailById(java.util.UUID emailId) {
        return emailRepository.findById(emailId)
                .orElseThrow(() -> new RuntimeException("Email não encontrado com ID: " + emailId));
    }
}
