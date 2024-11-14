package org.example.expert.security;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class TokenNotValidateException extends RuntimeException {

    private final HttpStatus status;

    public TokenNotValidateException(String message,
        HttpStatus status) {
        super(message);
        this.status = status;
    }
}
