package com.lckjsoft.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * @Author: 一个逗比程序员
 * @Project: lz-ac
 * @Description:  配置网关跨域
 * @Date: Created in    2021/5/16 14:27
 * @Modified By:
 * @Modified Date:      2021/5/16
 */
@Configuration
public class CorsConfig {
    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        /** 是什么请求方法，比如 GET POST PUT DELATE ..... */
        config.addAllowedMethod("*");
        /** 来自哪个域名的请求，*号表示所有 */
        config.addAllowedOrigin("*");
        /** 是什么请求头 */
        config.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
