package com.java10x.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class EmailDto{
    private UUID userId;
    private String emailTo;
    private String emailSubject;
    private String body;
}
