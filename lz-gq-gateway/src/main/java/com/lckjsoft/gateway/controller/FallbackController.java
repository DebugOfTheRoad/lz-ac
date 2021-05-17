package com.lckjsoft.gateway.controller;

import com.lckjsoft.common.base.JsonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {
	
	@GetMapping("/fallback")
	public JsonResult<?> fallback() {
		return JsonResult.error("服务暂时不可用");
	}
	
}
