package com.lckjsoft.auth.init;


import com.lckjsoft.auth.mapper.*;
import com.lckjsoft.auth.oauth2.SSOClientDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
@Component
@Slf4j
public class OAuth2AdminStarter implements ApplicationRunner {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    @Autowired
    private RoleUserMapper roleUserMapper;
    @Autowired
    private ClientRegistrationService clientRegistrationService;
    @Value("${oauth2.init-admin.username:chench}")
    private String username;
    @Value("${oauth2.init-admin.password:123456}")
    private String password;
    @Value("${oauth2.init-admin.rolename:admin}")
    private String rolename;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(userMapper.checkName(username)==null){
            password = passwordEncoder.encode(password);
            List<String> authoritieList = Arrays.asList("admin".split(";"));
            List<String> resourceList = new ArrayList<>();
            List<String> scopeList = new ArrayList<>();
            SSOClientDetails clientDetails = new SSOClientDetails();
            clientDetails.setClientId(username);
            clientDetails.setClientSecret(password);
            //clientDetails.setRole(roleName);
            Set<String> scope = new HashSet<String>();
            scope.addAll(scopeList);
            scope.add("read");
            scope.add("write");
            clientDetails.setScope(scope);

            Set<String> types = new TreeSet<String>();
            types.add("password");
            types.add("authorization_code");
            types.add("refresh_token");
            clientDetails.setTypes(types);

            Set<String> resourceIds = new HashSet<String>();
            resourceIds.add("oauth");
            resourceIds.addAll(resourceList);
            //resourceIds.add("user");
            clientDetails.setResourceIds(resourceIds);

            Map<String, Object> additionalInformation = new HashMap<String, Object>();
            additionalInformation.put("time", System.currentTimeMillis());
            clientDetails.setAdditionalInformation(additionalInformation);

            Set<String> authSets = new HashSet<String>();
            authSets.addAll(authoritieList);
//        roleSets.add("oauth");
//        roleSets.add("user");
            List<GrantedAuthority> grantedAuthorities = AuthorityUtils.createAuthorityList(authSets.toArray(new String[0]));
            clientDetails.setAuthorities(grantedAuthorities);

            Set<String> autoApproveScopes = new HashSet<String>();
            autoApproveScopes.add("true");
            clientDetails.setAutoApproveScopes(autoApproveScopes);
            clientRegistrationService.addClientDetails(clientDetails);
            userMapper.add(username, password);
            roleMapper.add(rolename, 1);
            permissionMapper.add("oauth2鉴权账号注册", "/oauth/registry", "POST");
            String roleId = roleMapper.getId(rolename);
            String userId = userMapper.getId(username);
            roleUserMapper.add(roleId, userId);
            String permissionId = permissionMapper.getId("/oauth/registry");
            rolePermissionMapper.add(roleId, permissionId);
        }
        log.info("oauth2鉴权账号注册", "初始化成功");
    }

}
