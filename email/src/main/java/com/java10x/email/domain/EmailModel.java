package com.java10x.email.domain;

import com.java10x.email.enums.EmailStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_EMAIL")
@Data
@RequiredArgsConstructor
public class EmailModel {

    private final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID emailId;
    private UUID userId;
    private String emailFrom;
    private String emailTo;
    private String emailSubject;
    @Column(columnDefinition = "TEXT")
    private String body;
    private LocalDateTime sendDateEmail;
    private EmailStatus emailStatus;
}
