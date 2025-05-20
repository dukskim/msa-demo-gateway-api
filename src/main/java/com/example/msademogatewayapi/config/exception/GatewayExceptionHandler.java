package com.example.msademogatewayapi.config.exception;

import com.example.msademogatewayapi.base.dto.ErrorResult;
import com.example.msademogatewayapi.base.enums.ErrorCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.info("in GATEWAY Exception handler : {}", String.valueOf(ex));
        ErrorData errorData;
        int errorCode = -1;
        String errorMessage;

        if (ex.getClass() == GatewayException.class) {
            GatewayException e = (GatewayException) ex;
            errorCode = ErrorCode.valueOf(e.getMessage()).getCode();
            errorMessage = ErrorCode.valueOf(e.getMessage()).getMessage();
            errorData = ErrorData.builder().code(errorCode).message(errorMessage).build();
        }else if(ex.getClass() == GatewayStatusException.class){
            try {
                String err = ex.getMessage();
                String[] arr = err.split(" ");  //ex) "504 GATEWAY_TIMEOUT" 형식. 공백으로 분리
                errorCode = Integer.valueOf(arr[0]);
                errorMessage = arr[1];
                errorData = ErrorData.builder().code(errorCode).message(errorMessage).build();
            }catch(RuntimeException e){
                throw new GatewayException(ErrorCode.GATEWAY_SERVER_ERROR);
            }
        } else {
            errorData = new ErrorData(ErrorCode.UNKNOWN_ERROR);
        }

        byte[] bytes;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            bytes = (objectMapper.writeValueAsString(new ErrorResult(errorData))).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            throw new GatewayException(ErrorCode.GATEWAY_SERVER_ERROR);
        }
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        if(ex.getClass() == GatewayStatusException.class) {
            if (errorCode == ErrorCode.EXPIRED_TOKEN.getCode())
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            else if  (errorCode == ErrorCode.AUTHORIZATION_FAIL.getCode())
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            else
                exchange.getResponse().setRawStatusCode(errorData.getCode());
        }else if(ex.getClass() == GatewayException.class) {
            try {
                if (errorCode == ErrorCode.EXPIRED_TOKEN.getCode())
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                else if  (errorCode == ErrorCode.AUTHORIZATION_FAIL.getCode())
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                else
                    exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                log.debug(e.getMessage());
                exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            }
        }else {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
        }
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }
}