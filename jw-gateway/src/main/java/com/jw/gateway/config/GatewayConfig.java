package com.jw.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * @description:
 * @date: 2020/8/11
 * @author: jinwei
 */
@Configuration
public class GatewayConfig {
    @Bean
    KeyResolver userKeyResolver() {
        //设置请求参数头
        return exchange -> Mono.just(exchange.getRequest().getQueryParams().getFirst("jw"));
    }

//    @Bean
//    public KeyResolver ipKeyResolver() {
//        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
//    }

//    @Bean
//    KeyResolver apiKeyResolver() {
//        return exchange -> Mono.just(exchange.getRequest().getPath().value());
//    }
}
