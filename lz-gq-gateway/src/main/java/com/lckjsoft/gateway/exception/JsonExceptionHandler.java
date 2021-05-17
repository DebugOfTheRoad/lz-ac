package com.lckjsoft.gateway.exception;

import cn.hutool.core.lang.Assert;
import com.lckjsoft.common.base.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.cloud.gateway.support.TimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonExceptionHandler implements ErrorWebExceptionHandler {

	/**
	 * MessageReader
	 */
	private List<HttpMessageReader<?>> messageReaders = Collections.emptyList();
	/**
	 * MessageWriter
	 */
	private List<HttpMessageWriter<?>> messageWriters = Collections.emptyList();
	/**
	 * ViewResolvers
	 */
	private List<ViewResolver> viewResolvers = Collections.emptyList();
	/**
	 * 存储处理异常后的信息
	 */
	private ThreadLocal<Map<String, Object>> exceptionHandlerResult = new ThreadLocal<>();
	/**
	 * 参考AbstractErrorWebExceptionHandler
	 */
	public void setMessageReaders(List<HttpMessageReader<?>> messageReaders) {
		Assert.notNull(messageReaders, "'messageReaders' must not be null");
		this.messageReaders = messageReaders;
	}

	/**
	 * 参考AbstractErrorWebExceptionHandler
	 */
	public void setViewResolvers(List<ViewResolver> viewResolvers) {
		this.viewResolvers = viewResolvers;
	}

	/**
	 * 参考AbstractErrorWebExceptionHandler
	 */
	public void setMessageWriters(List<HttpMessageWriter<?>> messageWriters) {
		Assert.notNull(messageWriters, "'messageWriters' must not be null");
		this.messageWriters = messageWriters;
	}

	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
		// 按照异常类型进行处理
		HttpStatus httpStatus = null;
		StringBuilder body = new StringBuilder();
		if (ex instanceof NotFoundException) {
			httpStatus = HttpStatus.NOT_FOUND;
			body.append("Service Not Found");
		}else if (ex instanceof TimeoutException) {
			httpStatus = HttpStatus.GATEWAY_TIMEOUT;
			body.append("Service Request Timeout");
		}else{
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			body.append(ex.getMessage());
		}
		Throwable ex2 = ex.getCause();
		if(ex2!=null){
			body.append("<--:"+ex2.getMessage());
		}
		ServerHttpRequest request = exchange.getRequest();
		String path = request.getPath().value();
		String methodName = request.getMethodValue();
		String ip = request.getRemoteAddress().getAddress().toString();
		// 错误记录
		log.error("[全局异常处理]异常请求路径:", path,
				"执行方法:", methodName, 
				"ip:", ip, 
				"记录异常信息:", body.toString());
		// 参考AbstractErrorWebExceptionHandler
		if (exchange.getResponse().isCommitted()) {
			return Mono.error(ex);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>(2);
		resultMap.put("requestId", request.getId());
		resultMap.put("path", path);
		resultMap.put("method", methodName);
		resultMap.put("ip", ip);
		//resultMap.put("errorMessage", LoggerUtil.getErrorMessage(ex));
		exceptionHandlerResult.set(JsonResult.resultMap(httpStatus.value(), body.toString(), resultMap));
		ServerRequest newRequest = ServerRequest.create(exchange, this.messageReaders);
		return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse).route(newRequest)
				.switchIfEmpty(Mono.error(ex)).flatMap((handler) -> handler.handle(newRequest))
				.flatMap((response) -> write(exchange, response));

	}

	/**
	 * 参考DefaultErrorWebExceptionHandler
	 */
	@SuppressWarnings("deprecation")
	protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
		Map<String, Object> result = exceptionHandlerResult.get();
		return ServerResponse.status((HttpStatus) HttpStatus.valueOf(((int)result.get("code"))))
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.body(BodyInserters.fromObject(result));
	}

	/**
	 * 参考AbstractErrorWebExceptionHandler
	 */
	private Mono<? extends Void> write(ServerWebExchange exchange, ServerResponse response) {
		exchange.getResponse().getHeaders().setContentType(response.headers().getContentType());
		return response.writeTo(exchange, new ResponseContext());
	}

	/**
	 * 参考AbstractErrorWebExceptionHandler
	 */
	private class ResponseContext implements ServerResponse.Context {
		@Override
		public List<HttpMessageWriter<?>> messageWriters() {
			return JsonExceptionHandler.this.messageWriters;
		}
		@Override
		public List<ViewResolver> viewResolvers() {
			return JsonExceptionHandler.this.viewResolvers;
		}
	}

}