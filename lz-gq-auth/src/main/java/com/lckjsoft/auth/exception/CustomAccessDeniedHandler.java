package com.lckjsoft.auth.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lckjsoft.common.base.JsonResult;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("customAccessDeniedHandler")
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
					   AccessDeniedException accessDeniedException)
			throws IOException, ServletException {
		int code = HttpServletResponse.SC_UNAUTHORIZED;
		String msg = "权限不足";
		String data = accessDeniedException.getMessage();
		ObjectMapper mapper = new ObjectMapper();
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.getWriter().write(mapper.writeValueAsString(
				JsonResult.result(code, msg, data)));
	}
}