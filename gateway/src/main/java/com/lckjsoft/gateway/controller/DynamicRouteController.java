package com.lckjsoft.gateway.controller;

import com.lckjsoft.gateway.constant.WebRequestRoutePrefix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * 查询网关的路由信息
 * @author uid40330
 */
@RestController
@RequestMapping(WebRequestRoutePrefix.ROUTE_PREFIX +"/route")
public class DynamicRouteController {

    @Autowired
    private RouteDefinitionLocator routeDefinitionLocator;

    /** 获取网关所有的路由信息*/
    @GetMapping("/routes")
    public Flux<RouteDefinition> getRouteDefinitions(){
        return routeDefinitionLocator.getRouteDefinitions();
    }
}
