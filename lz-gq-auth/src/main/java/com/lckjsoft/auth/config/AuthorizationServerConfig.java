package com.lckjsoft.auth.config;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import com.lckjsoft.auth.oauth2.JwtTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;
    //自定义登录或者鉴权失败时的返回信息
    @Resource(name = "webResponseExceptionTranslator")
    private WebResponseExceptionTranslator<OAuth2Exception> webResponseExceptionTranslator;
    
    //######################redis################################
    /*@Autowired
    private RedisConnectionFactory redisConnectionFactory;
    @Bean
    public TokenStore tokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }*/
    //#######################mysql###############################
    @Autowired
    private DataSource dataSource;
    @Autowired
    private UserDetailsService userDetailsService;
    @Bean
    public JdbcClientDetailsService jdbcClientDetailsService() {
        return new JdbcClientDetailsService(dataSource);
    }
    /*@Bean
	public TokenStore tokenStore() {
	    return new JdbcTokenStore(dataSource);
	}*/
    //#######################jwt###############################
    @Autowired
    private JwtTokenEnhancer jwtTokenEnhancer;

    /**
     * 把之前JdbdTokenStore 改成JwtTokenStore 就可以了
     * @return
     */
	@Bean
	public TokenStore tokenStore() {	
	    //使用Jwt操作token
	    return new JwtTokenStore(jwtAccessTokenConverter());
	}

    /**
     * 	生成jks
     * 	keytool -genkey -alias chenchjwt -keyalg RSA -keystore oauth2-jwt.jks
     * 	导出公钥-复制到文件中
     * 	keytool -list -rfc --keystore mytest.jks | openssl x509 -inform pem -pubkey
     * @return
     */
	@Bean
	public JwtAccessTokenConverter jwtAccessTokenConverter() {
	    JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyPair());
	    //converter.setSigningKey("12345@Chench#54321");//这里采用12345Chench54321作为生成密钥的key
	    return converter;
	}
	@Bean
	public KeyPair keyPair() {
        org.springframework.core.io.Resource resource = new ClassPathResource("keys/oauth2-jwt.jks");
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource, "Chench@2020".toCharArray());
	    return keyStoreKeyFactory.getKeyPair("chenchjwt");
	}
	//#######################end###############################
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    	TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> delegates = new ArrayList<TokenEnhancer>();
        //配置JWT的内容增强器
        delegates.add(jwtTokenEnhancer);
        delegates.add(jwtAccessTokenConverter());
        enhancerChain.setTokenEnhancers(delegates);
        // 存数据库
        endpoints.tokenStore(tokenStore())
        		.tokenEnhancer(enhancerChain)
        		.authenticationManager(authenticationManager)
        		//#######################mysql###############################
                .userDetailsService(userDetailsService)
                .exceptionTranslator(webResponseExceptionTranslator)
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST)
        		//#######################jwt###############################
                // 配置JwtAccessToken转换器
                .accessTokenConverter(jwtAccessTokenConverter());
        
        // 配置tokenServices参数
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(endpoints.getTokenStore());
        //开启支持refresh_token，此处如果之前没有配置，启动服务后再配置重启服务，可能会导致不返回token的问题，解决方式：清除redis对应token存储
        tokenServices.setSupportRefreshToken(true);
        //复用refresh_token
        tokenServices.setReuseRefreshToken(true);
        tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
        tokenServices.setTokenEnhancer(endpoints.getTokenEnhancer());
        //token有效期，设置2小时
        tokenServices.setAccessTokenValiditySeconds((int)TimeUnit.HOURS.toSeconds(2));
        //refresh_token有效期7天
        tokenServices.setRefreshTokenValiditySeconds((int)TimeUnit.DAYS.toSeconds(7));
        endpoints.tokenServices(tokenServices);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
    	/**
         * 配置oauth2服务跨域
         */
        CorsConfigurationSource source = new CorsConfigurationSource() {
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration corsConfiguration = new CorsConfiguration();
                corsConfiguration.addAllowedHeader("*");
                corsConfiguration.addAllowedOrigin(request.getHeader(HttpHeaders.ORIGIN));
                corsConfiguration.addAllowedMethod("*");
                corsConfiguration.setAllowCredentials(true);
                corsConfiguration.setMaxAge(3600L);
                return corsConfiguration;
            }
        };
        security.allowFormAuthenticationForClients()
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .addTokenEndpointAuthenticationFilter(new CorsFilter(source));
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    	//##########################mysql##########################
    	clients.withClientDetails(jdbcClientDetailsService());//设置客户端的配置从数据库中读取，存储在oauth_client_details表
    	//########################redis#####################
    	/*clients.inMemory()
        .withClient("chench")
        .secret(new BCryptPasswordEncoder().encode("123456"))
        .accessTokenValiditySeconds((int)TimeUnit.HOURS.toSeconds(2))
        .refreshTokenValiditySeconds((int)TimeUnit.DAYS.toSeconds(7))
        //.redirectUris("http://localhost:9501/login") //单点登录时配置
        .autoApprove(true)//自动授权配置
        .scopes("user")
        .authorizedGrantTypes("authorization_code", "password","refresh_token");
    	//.and().withClient() // 可以继续配置新的 Client
        ;*/
        //########################jwt#####################
        /*clients.inMemory()
        .withClient("chench").secret(new BCryptPasswordEncoder().encode("123456")) // Client 账号、密码。
        .accessTokenValiditySeconds((int)TimeUnit.HOURS.toSeconds(2))
        .refreshTokenValiditySeconds((int)TimeUnit.DAYS.toSeconds(7))
        .authorizedGrantTypes("authorization_code", "password","refresh_token") // 密码模式
        .scopes("user") // 可授权的 Scope
        //.and().withClient() // 可以继续配置新的 Client
        ;*/
    }
}