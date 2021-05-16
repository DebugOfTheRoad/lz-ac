package com.lckjsoft.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Author: 一个逗比程序员
 * @Project: lz-ac
 * @Description: q
 * @Date: Created in    2021/5/16 14:31
 * @Modified By:
 * @Modified Date:      2021/5/16
 */
@Slf4j
@Component
public class LzGlobalFilter implements GlobalFilter, Ordered {
    /**
     * Process the Web request and (optionally) delegate to the next {@code WebFilter}
     * through the given {@link GatewayFilterChain}.
     *
     * @param exchange the current server exchange
     * @param chain    provides a way to delegate to the next filter
     * @return {@code Mono<Void>} to indicate when request processing is complete
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("全局Filter请求......");

        MultiValueMap<String, String> valueMap = exchange.getRequest().getQueryParams();
        valueMap.forEach((k, v) -> {
            log.info("全局Filter拦截参数 {} ", k);
            v.forEach(s -> {
                log.info("全局Filter拦截参数值 = {} ", s);
            });
        });
        return chain.filter(exchange);
    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
