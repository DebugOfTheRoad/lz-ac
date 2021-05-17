package com.lckjsoft.auth.exception;

import com.lckjsoft.common.base.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.naming.AuthenticationException;

/**
 * @author uid40330
 */
@ControllerAdvice
@Slf4j
public class WsExceptionHandler {
	
    @ResponseBody
    @ExceptionHandler(value = {Exception.class, RuntimeException.class,
            OAuth2Exception.class, AuthenticationException.class})
    public JsonResult<?> handleOauth2(RuntimeException e) {
    	log.error(String.valueOf(e));
        return JsonResult.error(e.getMessage());
    }
}
