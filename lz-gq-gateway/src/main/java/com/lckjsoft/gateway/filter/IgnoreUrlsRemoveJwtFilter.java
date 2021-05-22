//package com.lckjsoft.gateway.filter;
//
//import com.lckjsoft.common.constant.OAuth2Constant;
//import com.lckjsoft.gateway.config.IgnoreUrlsConfig;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.util.AntPathMatcher;
//import org.springframework.util.PathMatcher;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//
///**
// * 白名单路径访问时需要移除JWT请求头
// */
//@Component
//public class IgnoreUrlsRemoveJwtFilter implements WebFilter {
//
//    @Autowired
//    private IgnoreUrlsConfig ignoreUrlsConfig;
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//    	ServerHttpRequest request = exchange.getRequest();
//        String uri = request.getURI().getPath();
//        PathMatcher pathMatcher = new AntPathMatcher();
//        //白名单路径移除JWT请求头
//        List<String> ignoreUrls = ignoreUrlsConfig.getIgnoreurls();
//        for (String ignoreUrl : ignoreUrls) {
//            if (pathMatcher.match(ignoreUrl, uri)) {
//                //request = request.mutate().header(OAuth2Constant.HEADER_AUTHORIZATION, "").build();
//                //exchange = exchange.mutate().request(request).build();
//                exchange.getRequest().mutate().header(OAuth2Constant.HEADER_AUTHORIZATION, "").build();
//                return chain.filter(exchange);
//            }
//        }
//        return chain.filter(exchange);
//    }
//}
