package com.lckjsoft.auth.exception;

import com.lckjsoft.common.base.JsonResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;

@Configuration
public class WebResponseExceptionTranslateConfig{
	/**
	 * 自定义登录或者鉴权失败时的返回信息
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Bean(name = "webResponseExceptionTranslator")
	public WebResponseExceptionTranslator webResponseExceptionTranslator() {
		return new DefaultWebResponseExceptionTranslator() {
			@Override
			public ResponseEntity translate(Exception e) throws Exception {
				ResponseEntity<?> responseEntity = super.translate(e);
				OAuth2Exception body = (OAuth2Exception) responseEntity.getBody();
				HttpHeaders headers = new HttpHeaders();
				headers.setAll(responseEntity.getHeaders().toSingleValueMap());
				String message = e.getMessage();
				if ("Bad credentials".equalsIgnoreCase(body.getMessage())
						|| message.toLowerCase().contains("bad credentials")) {
					return new ResponseEntity(
							JsonResult.fail("账号或密码错误"), headers, HttpStatus.OK);
				}
				body.addAdditionalInformation("code",
						responseEntity.getStatusCode().toString());
				body.addAdditionalInformation("message", message);
				return new ResponseEntity(body, headers, responseEntity.getStatusCode());
			}
		};
	}
}