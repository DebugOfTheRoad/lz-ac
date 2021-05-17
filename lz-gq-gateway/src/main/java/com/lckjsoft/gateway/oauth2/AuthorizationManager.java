package com.lckjsoft.gateway.oauth2;


import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONObject;
import com.lckjsoft.common.constant.OAuth2Constant;
import com.lckjsoft.common.constant.RedisConstant;
import com.lckjsoft.common.util.NullUtil;
import com.lckjsoft.redis.service.JedisService;
import com.nimbusds.jose.JWSObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 鉴权管理器，用于判断是否有资源的访问权限
 */
@Component
@Slf4j
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    //@Autowired
    //private RedisTemplate<String,Object> redisTemplate;
    @Autowired
    private JedisService jedisService;

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono, AuthorizationContext authorizationContext) {
        ServerHttpRequest request = authorizationContext.getExchange().getRequest();
        String path = request.getURI().getPath();
        int idx = path.lastIndexOf("/");
        if(idx > 0){//有多个的时候进行截取
            path = path.substring(path.indexOf("/", path.indexOf("/")+1));//获取第二个/开始的地址
        }
        log.info("请求地址:", path);
        List<String> authorities = new ArrayList<String>(0);
        String token = request.getHeaders().getFirst(OAuth2Constant.HEADER_AUTHORIZATION);
        log.info("AuthorizationManager.token:", token);
        if (NullUtil.isNotNull(token)) {
            try {//从token中解析用户信息并设置到Header中去
                String realToken = token.replace("Bearer ", "");
                JWSObject jwsObject = JWSObject.parse(realToken);
                String userStr = jwsObject.getPayload().toString();
                log.info("AuthorizationManager.user:", userStr);
                JSONObject userJson = new JSONObject(userStr);
                String name = userJson.getStr("user_name");
                log.info("AuthorizationManager.name:", name);
                String roleNames = jedisService.get(RedisConstant.OAUTH2_USER_PERMISSIONS + name +":"+ path);
                if(NullUtil.isNotNull(roleNames)){
                    authorities = Convert.toList(String.class, roleNames.split(","));
                    authorities = authorities.stream().map(i -> i = OAuth2Constant.AUTHORITY_PREFIX + i).collect(Collectors.toList());
                    if(NullUtil.isNotNull(userStr)){
                        //设置用户信息到请求头
                        request = request.mutate().header(OAuth2Constant.HEADER_OAUTH_USER, userStr).build();
                        authorizationContext.getExchange().mutate().request(request).build();
                    }
                }
            } catch (java.text.ParseException e) {
                log.warn("AuthorizationManager.JwtParse.error:", e.getMessage(), token);
            }
        }
        //认证通过且角色匹配的用户可访问当前路径
        return mono
                .filter(Authentication::isAuthenticated)
                .flatMapIterable(Authentication::getAuthorities)
                .map(GrantedAuthority::getAuthority)
                .any(authorities::contains)
                .map(AuthorizationDecision::new)
                .defaultIfEmpty(new AuthorizationDecision(false));
    }

}
