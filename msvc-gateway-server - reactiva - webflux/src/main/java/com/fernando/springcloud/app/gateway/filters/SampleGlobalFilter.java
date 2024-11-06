package com.fernando.springcloud.app.gateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
public class SampleGlobalFilter implements GlobalFilter, Ordered {

    private final Logger logger = LoggerFactory.getLogger(SampleGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        
        logger.info("Ejecutando el filtro antes del request PRE");

        // exchange.getRequest().mutate().headers(h -> h.add("token", "abcdefg"));
        
         // Create a mutable copy of the request headers
         HttpHeaders headers = new HttpHeaders();
         headers.putAll(exchange.getRequest().getHeaders());
         headers.add("token", "abcdefg");
 
         // Use a decorated request with the modified headers
         ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
            @SuppressWarnings("null")
            @Override
             public HttpHeaders getHeaders() {
                 return headers;
             }
         };
 
         // Mutate the exchange with the new request
         ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

         return chain.filter(mutatedExchange).then(Mono.fromRunnable(() -> {
        // return chain.filter(exchange).then(Mono.fromRunnable(()->{
            logger.info("Ejecutando filtro POST response");

            // String token = exchange.getRequest().getHeaders().getFirst("token");
            String token = mutatedExchange.getRequest().getHeaders().getFirst("token");
            if(token != null){
                logger.info("token: " + token);
                exchange.getResponse().getHeaders().add("token", token);
            }

            Optional.ofNullable(mutatedExchange.getRequest().getHeaders().getFirst("token"))
            // Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("token"))
                .ifPresent(value -> {
                    logger.info("token2: " + value);
                    exchange.getResponse().getHeaders().add("token2", value);
            });

            exchange.getResponse().getCookies().add("color", ResponseCookie.from("color", "red").build());
            // exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
        }));
    }

    @Override
    public int getOrder() {
        return 100;
    }

}
