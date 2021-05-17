package com.lckjsoft.auth.oauth2;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author uid40330
 */
public class SSOClientDetails implements ClientDetails {

	private static final long serialVersionUID = -4453033309411911793L;
	private String clientId;
	private String clientSecret;
	private String role;
	/** token有效时间 */
	private Integer accessTokenValiditySeconds = 7200;
	/** 刷新token有效时间 */
	private Integer refreshTokenValiditySeconds = 7*24*60*60;
	/** 客户端权限范围 */
	private Set<String> scope;
	/** 客户端可请求的认证类型 */
	private Set<String> types;
	private Set<String> autoApproveScopes;
	private Set<String> resourceIds;
	/** 补充信息json */
	private Map<String, Object> additionalInformation;
	/**  跳转地址 */
	private Set<String> registeredRedirectUris;
	/** 权限 */
	private List<GrantedAuthority> authorities;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	@Override
	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public Set<String> getScope() {
		return scope;
	}

	public void setScope(Set<String> scope) {
		this.scope = scope;
	}

	public Set<String> getTypes() {
		return types;
	}

	public void setTypes(Set<String> types) {
		this.types = types;
	}

	public Set<String> getAutoApproveScopes() {
		return autoApproveScopes;
	}

	public void setAutoApproveScopes(Set<String> autoApproveScopes) {
		this.autoApproveScopes = autoApproveScopes;
	}

	@Override
	public Set<String> getResourceIds() {
		return resourceIds;
	}

	public void setResourceIds(Set<String> resourceIds) {
		this.resourceIds = resourceIds;
	}

	@Override
	public Map<String, Object> getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(Map<String, Object> additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	public Set<String> getRegisteredRedirectUris() {
		return registeredRedirectUris;
	}

	public void setRegisteredRedirectUris(Set<String> registeredRedirectUris) {
		this.registeredRedirectUris = registeredRedirectUris;
	}

	@Override
	public List<GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

	@Override
	public boolean isSecretRequired() {
		return this.clientSecret != null;
	}

	@Override
	public boolean isScoped() {
		return this.scope != null && !this.scope.isEmpty();
	}

	@Override
	public Set<String> getAuthorizedGrantTypes() {
		return this.types;
	}

	@Override
	public Set<String> getRegisteredRedirectUri() {
		return this.registeredRedirectUris;
	}

	@Override
	public Integer getAccessTokenValiditySeconds() {
		 return this.accessTokenValiditySeconds;
	}

	@Override
	public Integer getRefreshTokenValiditySeconds() {
		return this.refreshTokenValiditySeconds;
	}

	@Override
	public boolean isAutoApprove(String scope) {
		if(this.autoApproveScopes == null) {
            return false;
        } else {
            Iterator<String> var2 = this.autoApproveScopes.iterator();
            String auto;
            do {
                if(!var2.hasNext()) {
                    return false;
                } 
                auto = (String)var2.next();
            } while(!auto.equals("true") && !scope.matches(auto)); 
            return true;
        }
	}

}
