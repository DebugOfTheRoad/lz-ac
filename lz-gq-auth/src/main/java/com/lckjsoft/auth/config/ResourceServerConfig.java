//package com.lckjsoft.auth.config;
//
//
//import cn.hutool.core.util.ArrayUtil;
//import com.lckjsoft.auth.exception.AuthExceptionEntryPoint;
//import com.lckjsoft.auth.exception.CustomAccessDeniedHandler;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
//import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
//import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
//import org.springframework.security.oauth2.provider.token.TokenStore;
//
///**
// * 优先级默认是3
// * 比WebSecurityConfig大
// */
//@Configuration
//@EnableResourceServer
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
//public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
//
//    @Autowired
//    private IgnoreUrlsConfig ignoreUrlsConfig;
//    @Autowired
//    private TokenStore tokenStore;
//
//    //######################redis################################
//    //@Autowired
//    //private RedisConnectionFactory redisConnectionFactory;
//
//    @Override
//    public void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests().requestMatchers(
//                EndpointRequest.toAnyEndpoint())
//                //antMatcher表示只能处理/oauth的请求
//                .permitAll()
//                .antMatchers(
//                        ArrayUtil.toArray(ignoreUrlsConfig.getIgnoreurls(), String.class))
//                //不需要权限
//                .permitAll()
//                //需要权限
//                .anyRequest().authenticated()
//                .and().csrf().disable()
//        ;
////      .antMatchers("user/test2").hasRole("USER")
////      .anyRequest().authenticated()
//        System.out.println("ResourceServerConfig......");
//    }
//
//    @Override
//    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
//        resources
//                .tokenStore(tokenStore)
//                //重点，设置资源id
//                .resourceId("oauth")
//                .authenticationEntryPoint(new AuthExceptionEntryPoint())
//                .accessDeniedHandler(new CustomAccessDeniedHandler());
//    }
//
//    //######################redis################################
//    /*@Bean
//    public TokenStore tokenStore() {
//    	RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
//        redisTokenStore.setPrefix("chench-token:");
//        return redisTokenStore;
//	}*/
//}
