package com.java10x.user.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("Usuário com o email '" + email + "' já existe no sistema.");
    }

    public DuplicateEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
