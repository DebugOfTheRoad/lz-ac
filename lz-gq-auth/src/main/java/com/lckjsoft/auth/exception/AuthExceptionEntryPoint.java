package com.lckjsoft.auth.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lckjsoft.common.base.JsonResult;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthExceptionEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
						 AuthenticationException authException) throws ServletException {
		int code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		String msg = authException.getMessage();
		if (authException.getCause() instanceof InvalidTokenException) {
			code = HttpServletResponse.SC_UNAUTHORIZED;
			msg = "凭证失效";
		}
		response.setContentType("application/json");
		response.setStatus(code);
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(response.getOutputStream(),
					JsonResult.result(code, msg));
		} catch (Exception e) {
			throw new ServletException();
		}
	}
}