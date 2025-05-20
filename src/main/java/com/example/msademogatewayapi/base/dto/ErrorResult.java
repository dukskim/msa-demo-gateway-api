package com.example.msademogatewayapi.base.dto;

import com.example.msademogatewayapi.config.exception.ErrorData;
import lombok.Builder;
import lombok.Data;

@Data
public class ErrorResult {
    private final ErrorData error;
    private final String result;
    private final String data;

    @Builder
    public ErrorResult(ErrorData data) {
        this.error = data;
        this.result = "FAIL";
        this.data = null;
    }

    @Override
    public String toString() {
        //    private String errorCodeMaker(int errorCode, String msg) {
//        return "{\"result\":\"FAIL\", \"error\": { \"code\":" + errorCode +", \"message\":\""+msg+"\"}, \"data\": null}";
//    }
        return "{\"result\":\""+result+"\", \"error\": { \"code\":" + error.getCode() +", \"message\":\""+error.getMessage()+"\"}, \"data\": null}";
    }
}
