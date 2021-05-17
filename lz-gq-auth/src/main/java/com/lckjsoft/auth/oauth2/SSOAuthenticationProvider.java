package com.lckjsoft.auth.oauth2;


import com.lckjsoft.auth.mapper.UserMapper;
import com.lckjsoft.auth.service.SSOUserDetailsService;
import com.lckjsoft.common.constant.RedisConstant;
import com.lckjsoft.common.util.NullUtil;
import com.lckjsoft.redis.service.JedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Component;

/**
 * @author uid40330
 */
@Component
@Slf4j
public class SSOAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private SSOUserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JedisService jedisService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        log.info("用户输入的用户名是：" + username);
        log.info("用户输入的密码是：" + authentication.getCredentials());
        if(NullUtil.isNull(username) || NullUtil.isNull(authentication.getCredentials())){
            throw new OAuth2Exception("账号或密码错误");
        }
        String disabled = jedisService.get(RedisConstant.OAUTH2_DISABLED+username);
        if(disabled!=null){
            throw new DisabledException("该账户已被禁用，请联系管理员!");
        }
        String locked = jedisService.get(RedisConstant.OAUTH2_LOCKED+username);
        if(locked!=null){
            throw new LockedException("该账号已被锁定，请联系管理员!");
        }
        String expired = jedisService.get(RedisConstant.OAUTH2_EXPIRED+username);
        if(expired!=null){
            throw new AccountExpiredException("该账号已失效，请联系管理员!");
        }
        // 根据用户输入的用户名获取该用户名已经在服务器上存在的用户详情，如果没有则返回null
        UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getName());
        log.info("服务器上已经保存的用户名是：" + userDetails.getUsername());
        log.info("服务器上保存的该用户名对应的密码是： " + userDetails.getPassword());
        log.info("服务器上保存的该用户对应的权限是：" + userDetails.getAuthorities());
        if(!passwordEncoder.matches(String.valueOf(authentication.getCredentials()), userDetails.getPassword())){
            String count = jedisService.get(RedisConstant.OAUTH2_LOGINFAIL+username);
            int num = Integer.valueOf(count==null?"0":count);
            num += 1;
            if(num>=6){
                //6次后锁定账号
                userMapper.locked(username);
                jedisService.set(RedisConstant.OAUTH2_LOCKED+username, "1", 10);
                jedisService.del(RedisConstant.OAUTH2_LOGINFAIL+username);
                throw new LockedException("该账号已被锁定，请联系管理员!");
            }else if(num>=3){
                jedisService.set(RedisConstant.OAUTH2_LOGINFAIL+username, String.valueOf(num), -1);
                throw new OAuth2Exception("账号或密码错误，剩余"+(6-num)+"次机会");
            }
            jedisService.set(RedisConstant.OAUTH2_LOGINFAIL+username, String.valueOf(num), -1);
            throw new OAuth2Exception("账号或密码错误");
            //如果验证不同过则返回null或者抛出异常
        }
        jedisService.del(RedisConstant.OAUTH2_LOGINFAIL+username);
        //验证成功  将返回一个UsernamePasswordAuthenticaionToken对象
        log.info(username, "认证成功!!!!!!");
        //分别返回用户实体   输入的密码   以及用户的权限
        return new UsernamePasswordAuthenticationToken(userDetails, authentication.getCredentials(), userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}