package com.example.msademogatewayapi.config.exception;

public class GatewayStatusException extends RuntimeException {
    public GatewayStatusException(String errorCode) {
        super(errorCode);
    }
}
