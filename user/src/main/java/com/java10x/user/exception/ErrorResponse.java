package com.java10x.user.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    private HttpStatus status;
    private int statusCode;
    private String message;
    private String error;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public ErrorResponse(HttpStatus status, String message, String error) {
        this.status = status;
        this.statusCode = status.value();
        this.message = message;
        this.error = error;
        this.timestamp = LocalDateTime.now();
    }
}
