package com.lckjsoft.auth.service;


import com.lckjsoft.auth.mapper.UserMapper;
import com.lckjsoft.common.constant.RedisConstant;
import com.lckjsoft.common.model.OAuthUser;
import com.lckjsoft.common.util.NullUtil;
import com.lckjsoft.redis.service.JedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class SSOUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JedisService jedisService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        OAuthUser user = userMapper.findByUserName(username);
        if(user==null){
            String count = jedisService.get(RedisConstant.OAUTH2_LOGINFAIL+username);
            int num = (count==null?0:Integer.valueOf(count)) + 1;
            if(num>=6){//6次后锁定账号
                jedisService.set(RedisConstant.OAUTH2_LOCKED+username, 1, -1);//不存在用户永久锁定,新增后解除
                jedisService.del(RedisConstant.OAUTH2_LOGINFAIL+username);
                throw new LockedException("该账号已被锁定，请联系管理员!");
            }else if(num>=3){
                jedisService.set(RedisConstant.OAUTH2_LOGINFAIL+username, num, -1);
                throw new OAuth2Exception("账号或密码错误，剩余"+(6-num)+"次机会");
            }else{
                jedisService.set(RedisConstant.OAUTH2_LOGINFAIL+username, num, -1);
                throw new OAuth2Exception("账号或密码错误");
            }
        }
        //启用=true
        boolean isEnabled = user.getEnabled()!=null && user.getEnabled().intValue()==1;
        //未锁定=true
        boolean unLocked = user.getLocked()==null || user.getLocked().intValue()==0;
        //未失效=true
        boolean unExpired = user.getValidtime()==null || user.getValidtime().getTime() > System.currentTimeMillis();
        if (!isEnabled) {
            jedisService.set(RedisConstant.OAUTH2_DISABLED+username, 1, 10);
            throw new DisabledException("该账户已被禁用，请联系管理员!");
        } else if (!unLocked) {
            jedisService.set(RedisConstant.OAUTH2_LOCKED+username, 1, 10);
            throw new LockedException("该账号已被锁定，请联系管理员!");
        } else if (!unExpired) {
            jedisService.set(RedisConstant.OAUTH2_EXPIRED+username, 1, 10);
            throw new AccountExpiredException("该账号已失效，请联系管理员!");
        }
        //删除旧数据
        jedisService.delKeys(RedisConstant.OAUTH2_USER_PERMISSIONS + username);
        String[] roles = new String[0];
        String roleNames = userMapper.getRoleNames(user.getId());
        if(NullUtil.isNotNull(roleNames)){
            roles = roleNames.split(",");
        }
        Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(roles);
        List<String> permissions = userMapper.getPermissions(user.getId());
        if(NullUtil.isNotNull(permissions)){
            //缓存用户权限和角色
            for (String permission: permissions) {
                jedisService.setDefault(RedisConstant.OAUTH2_USER_PERMISSIONS + username+":"+permission, roleNames);
            }
        }
        //缓存权限角色
        //jedisService.setDefault(RedisConstant.OAUTH2_USER_ROLES+username, user.getRoles());
        return new User(username, user.getPassword(), authorities);
    }

}