package com.example.msademogatewayapi.config.exception;

import com.example.msademogatewayapi.base.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class ErrorData {
    private final int code;
    private final String message;

    public ErrorData(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

}
