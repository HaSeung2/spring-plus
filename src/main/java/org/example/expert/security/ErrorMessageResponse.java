package org.example.expert.security;

import lombok.Getter;

@Getter
public class ErrorMessageResponse {
    private int errorCode;
    private String message;

    public ErrorMessageResponse(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
