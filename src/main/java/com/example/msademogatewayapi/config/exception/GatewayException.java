package com.example.msademogatewayapi.config.exception;


import com.example.msademogatewayapi.base.enums.ErrorCode;

public class GatewayException extends RuntimeException {
    public GatewayException(ErrorCode errorCode) {
        super(String.valueOf(errorCode));
    }
}
