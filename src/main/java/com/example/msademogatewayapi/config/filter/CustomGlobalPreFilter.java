package com.example.msademogatewayapi.config.filter;

import com.example.msademogatewayapi.base.enums.ErrorCode;
import com.example.msademogatewayapi.config.exception.GatewayException;
import com.example.msademogatewayapi.config.exception.GatewayStatusException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Slf4j
@Component
public class CustomGlobalPreFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return Mono.defer(() -> {
                    ServerHttpRequest response = exchange.getRequest();
                    log.info("Pre Filter: Request URI is {}", response.getURI());
                    // Add any custom logic here

                    return chain.filter(exchange);
                })
                .onErrorResume(error -> {
                    log.info("CustomGlobalFilter error => {}", error.getMessage());

                    if(error.getMessage().equals(ErrorCode.SERVER_RESPONSE_ERROR.toString())) {
                        throw new GatewayException(ErrorCode.SERVER_RESPONSE_ERROR);
                    } else if(error.getMessage().equals(ErrorCode.USER_ALREADY_LOGIN.toString())) {
                        throw new GatewayException(ErrorCode.USER_ALREADY_LOGIN);
                    } else if(error instanceof GatewayStatusException) {
                        throw new GatewayStatusException(error.getMessage());
                    }

                    return Mono.error(error);
//                    return chain.filter(exchange);
                });

    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
