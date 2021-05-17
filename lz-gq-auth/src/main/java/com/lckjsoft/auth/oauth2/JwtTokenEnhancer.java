package com.lckjsoft.auth.oauth2;

import com.lckjsoft.auth.mapper.UserMapper;
import com.lckjsoft.common.model.OAuthUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * jwt内容增强器
 */
@Component
public class JwtTokenEnhancer implements TokenEnhancer {

	@Autowired
    private UserMapper userMapper;

	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		Map<String, Object> additionalInfo = new HashMap<String, Object>();
		User user = (User) authentication.getUserAuthentication().getPrincipal();
		OAuthUser oAuthUser = userMapper.findByUserName(user.getUsername());
		additionalInfo.put("id", oAuthUser.getId());
		additionalInfo.put("name", oAuthUser.getName());
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
		return accessToken;
	}

}
