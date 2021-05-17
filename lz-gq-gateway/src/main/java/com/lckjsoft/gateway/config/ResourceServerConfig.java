package com.lckjsoft.gateway.config;

import cn.hutool.core.util.ArrayUtil;
import com.lckjsoft.common.constant.OAuth2Constant;
import com.lckjsoft.gateway.filter.IgnoreUrlsRemoveJwtFilter;
import com.lckjsoft.gateway.oauth2.AuthorizationManager;
import com.lckjsoft.gateway.oauth2.OauthAccessDeniedHandler;
import com.lckjsoft.gateway.oauth2.OauthAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
//@ConditionalOnBean(JwtDecoder.class)
public class ResourceServerConfig {
	@Autowired
	private AuthorizationManager authorizationManager;
	@Autowired
	private IgnoreUrlsRemoveJwtFilter ignoreUrlsRemoveJwtFilter;
	@Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;
	@Autowired
    private OauthAccessDeniedHandler oauthAccessDeniedHandler;
	@Autowired
    private OauthAuthenticationEntryPoint oauthAuthenticationEntryPoint;
	//@Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri:#{null}}")
	//private String jwkSetUri;
	
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    	//http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtDecoder(jwtDecoder())));
    	//http.oauth2ResourceServer().jwt();//.jwtAuthenticationConverter(jwtAuthenticationConverter());
    	http.oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthenticationConverter());
    	//http.oauth2ResourceServer(OAuth2ResourceServerSpec::jwt);
		//自定义处理JWT请求头过期或签名错误的结果
		http.oauth2ResourceServer().authenticationEntryPoint(oauthAuthenticationEntryPoint);
		//添加额外过滤器
		http.addFilterBefore(ignoreUrlsRemoveJwtFilter, SecurityWebFiltersOrder.AUTHENTICATION);
		http.authorizeExchange()
				//白名单配置
		        .pathMatchers(ArrayUtil.toArray(ignoreUrlsConfig.getIgnoreurls(), String.class)).permitAll()
				//鉴权管理器配置
		        .anyExchange().access(authorizationManager)
		        .and().exceptionHandling()
				//处理未授权
		        .accessDeniedHandler(oauthAccessDeniedHandler)
				//处理未认证
		        .authenticationEntryPoint(oauthAuthenticationEntryPoint)
		        .and().csrf().disable();
		return http.build();
    }
    
    /*@Bean
    public ReactiveJwtDecoder jwtDecoder() {
    	return NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }*/

	@Bean
	public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix(OAuth2Constant.AUTHORITY_PREFIX);
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(OAuth2Constant.AUTHORITY_CLAIM_NAME);
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
	}

}
