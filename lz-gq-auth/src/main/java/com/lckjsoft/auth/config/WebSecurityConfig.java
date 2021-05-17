package com.lckjsoft.auth.config;

import cn.hutool.core.util.ArrayUtil;
import com.lckjsoft.auth.oauth2.SSOAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Configuration
@EnableWebSecurity
//prePostEnabled = true, securedEnabled = true, jsr250Enabled = true
////开启可以在方法上面加权限控制的注解@PreAuthorize/@Secured
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
    private SSOAuthenticationProvider authenticationProvider;
	@Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/*@Bean
    public DaoAuthenticationProvider authentic	ationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        authenticationProvider.setHideUserNotFoundExceptions(false);
        return authenticationProvider;
    }*/

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		//目的是为了前端获取数据时获取到整个form-data的数据,提供验证器
        auth.authenticationProvider(authenticationProvider);
        //配置登录user验证处理器  以及密码加密器  好让认证中心进行验证
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//antMatcher表示只能处理/oauth2_token的请求
		http
				//定义哪些url需要被保护  哪些不需要保护
        .authorizeRequests()
        //"/oauth/token", "oauth/check_token", "oauth/error", "oauth/authorize", "oauth/confirm_access"
        .antMatchers(ArrayUtil.toArray(
				//定义这两个链接不需要登录可访问
        		ignoreUrlsConfig.getIgnoreurls(), String.class)).permitAll()
        //.antMatchers("/**").permitAll() //定义所有的都不需要登录  目前是测试需要
				//其他的都需要登录
        .anyRequest().authenticated()
				///sys/**下的请求 需要有admin的角色
        //.antMatchers("/sys/**").hasRole("admin")
				//如果未登录则跳转登录的页面   这儿可以控制登录成功和登录失败跳转的页面
        .and().formLogin().loginPage("/login")
				//定义号码与密码的parameter
        .usernameParameter("username").passwordParameter("password").permitAll()
				// 登出页
        .and().logout().logoutUrl("/logout").logoutSuccessUrl("/")
				//防止跨站请求spring security中默认开启
        .and().csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
	}

	/**
	 * 配置  支持passwod模式
	 * @return
	 * @throws Exception
	 */
	@Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
	
	/*@Override
    public void configure(WebSecurity web)  {
        // 将 check_token 暴露出去，否则资源服务器访问时报 403 错误
        web.ignoring().antMatchers("/oauth/check_token");
    }*/
    
}
