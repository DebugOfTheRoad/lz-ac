package com.lckjsoft.gateway.config;

import com.lckjsoft.common.constant.OAuth2Constant;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@Configuration
public class WsKeyResolver {

	@Bean("tokenKeyResolver")
	public KeyResolver tokenKeyResolver() {
		return exchange -> Mono.just(exchange.getRequest().getHeaders().getFirst(OAuth2Constant.HEADER_AUTHORIZATION));
	}

	@Primary
	@Bean("ipKeyResolver")
	public KeyResolver ipKeyResolver() {
		return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
	}

	@Bean("pathKeyResolver")
	public KeyResolver apiKeyResolver() {
		return exchange -> Mono.just(exchange.getRequest().getPath().value());
	}
	
}
