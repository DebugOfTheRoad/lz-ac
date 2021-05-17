package com.lckjsoft.auth.controller;

import com.lckjsoft.auth.mapper.RoleMapper;
import com.lckjsoft.auth.mapper.UserMapper;
import com.lckjsoft.auth.oauth2.SSOClientDetails;
import com.lckjsoft.common.base.JsonResult;
import com.lckjsoft.common.constant.RedisConstant;
import com.lckjsoft.redis.service.JedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientRegistrationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Api("oauth2接口")
@RestController
public class OauthController {

	@Autowired
    private UserMapper userMapper;
	@Autowired
	private RoleMapper roleMapper;
	@Autowired//(required=false)//mysql
	private ClientRegistrationService clientRegistrationService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	//@Autowired
    //private KeyPair keyPair;
	@Autowired
    private JedisService jedisService;
	//@Autowired
    //private TokenEndpoint tokenEndpoint;
	
	/*@ApiOperation(value="OAUTH2获取公钥", httpMethod="GET")
    @GetMapping("/oauth/publicKey")
    public Map<String, Object> getKey() {//jwt-公钥验证接口
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAKey key = new RSAKey.Builder(publicKey).build();
        return new JWKSet(key).toJSONObject();
    }
	
    @ApiOperation(value="OAUTH2获取用户信息", httpMethod="GET")
	@GetMapping("/oauth/current")
	public Principal user_current(Principal principal) {//token-用户权限验证接口
		return principal;
	}*/
    
    /*@PostMapping("/token")
    public JsonResult<OAuth2TokenVO> postAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        OAuth2AccessToken oAuth2AccessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();
        OAuth2TokenVO oauth2TokenDto = OAuth2TokenVO.builder()
                .token(oAuth2AccessToken.getValue())
                .refreshToken(oAuth2AccessToken.getRefreshToken().getValue())
                .expiresIn(oAuth2AccessToken.getExpiresIn())
                .tokenHead("Bearer ").build();
        return JsonResult.success(oauth2TokenDto);
    }*/
	
	//只注册授权账号
	
	@ApiOperation(value="oauth2鉴权账号注册", httpMethod="POST")
	@ApiImplicitParams({
		@ApiImplicitParam(name="username", value="用户名", required=true),
		@ApiImplicitParam(name="password", value="密码;已加密", required=true),
		@ApiImplicitParam(name="authorities", value="权限，多个以;隔开", required=true),
	})
	@PostMapping("/oauth/registry")
    public JsonResult<?> oauth_registry(
    		@RequestParam(name="username", required=true) String username,
    		@RequestParam(name="password", required=true) String password, 
    		@RequestParam(name="authorities", required=true) String authorities) {
    	if(userMapper.checkClientId(username)!=null){
    		return JsonResult.fail("账号已存在");
    	}
    	List<String> authoritieList = Arrays.asList(authorities.split(";"));
    	for (String authoritie : authoritieList) {
    		if(roleMapper.checkName(authoritie)==null){
        		return JsonResult.fail("角色【"+authoritie+"】不存在");
        	}
		}
		//Arrays.asList(resources.split(";"));
    	List<String> resourceList = new ArrayList<String>();
		//Arrays.asList(scopes.split(";"));
    	List<String> scopeList = new ArrayList<String>();
    	SSOClientDetails clientDetails = new SSOClientDetails();
    	//password = passwordEncoder.encode(password);
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
    	//userMapper.add(username, password);
		//删除锁定缓存
    	jedisService.del(RedisConstant.OAUTH2_LOCKED+username);
        return JsonResult.success();
    }
	
	
	//注册普通账号+授权账号
    //@PreAuthorize("hasAnyRole('normal','admin')")
    //@PreAuthorize("hasRole('admin')")
	@ApiOperation(value="oauth2鉴权账号注册(普通账号+授权账号)", httpMethod="POST")
	@ApiImplicitParams({
		@ApiImplicitParam(name="username", value="用户名", required=true),
		@ApiImplicitParam(name="password", value="密码", required=true),
		//@ApiImplicitParam(name="roleName", value="角色", required=true),
		//@ApiImplicitParam(name="scopes", value="范围，多个以;隔开", required=false),
		//@ApiImplicitParam(name="resources", value="资源，多个以;隔开", required=false),
		@ApiImplicitParam(name="authorities", value="权限，多个以;隔开", required=true),
	})
	@PostMapping("/oauth/registry2user")
    public JsonResult<?> oauth_registry2user(
    		@RequestParam(name="username", required=true) String username, //用户帐号
    		@RequestParam(name="password", required=true) String password, 
    		//@RequestParam(name="roleName", required=true) String roleName, 
    		//@RequestParam(name="scopes", required=false) String scopes,
    		//@RequestParam(name="resources", required=false) String resources,
    		@RequestParam(name="authorities", required=true) String authorities) {
    	if(userMapper.checkName(username)!=null || userMapper.checkClientId(username)!=null){
    		return JsonResult.fail("账号已存在");
    	}
    	List<String> authoritieList = Arrays.asList(authorities.split(";"));
    	for (String authoritie : authoritieList) {
    		if(roleMapper.checkName(authoritie)==null){
        		return JsonResult.fail("角色【"+authoritie+"】不存在");
        	}
		}
    	List<String> resourceList = new ArrayList<String>();//Arrays.asList(resources.split(";"));
    	List<String> scopeList = new ArrayList<String>();//Arrays.asList(scopes.split(";"));
    	SSOClientDetails clientDetails = new SSOClientDetails();
    	password = passwordEncoder.encode(password);
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
    	resourceIds.add("all");
    	resourceIds.addAll(resourceList);
		//resourceIds.add("user");
    	clientDetails.setResourceIds(resourceIds);
    	
    	Map<String, Object> additionalInformation = new HashMap<String, Object>();
    	additionalInformation.put("test", "123456");
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
    	jedisService.del(RedisConstant.OAUTH2_LOCKED+username);//删除锁定缓存
        return JsonResult.success();
    }
	
	
}
