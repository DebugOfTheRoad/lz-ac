package com.lckjsoft.gateway.constant;


/**
 * @author uid40330
 *
 * 网关转发时，路由前缀，方便做统一转发
 * 一般名字跟应用名字一致
 * 备注：
 * 微服务一般会又一个路由前缀
 * 例： 客户管理-新增 /customer/user/add
 * =========gateway 配置路由配置项=====
 *    routes:
 *   - id: service_customer
 *     uri: lb://CONSUMER
 *     order: 0
 *     predicates:
 *       - Path=/customer/**
 *     filters:
 *       - StripPrefix=1
 *       - AddResponseHeader=X-Response-Default-Foo, Default-Bar
 *  ==============================
 *  其中： StripPrefix=1的用途
 *  a:如果没有
 *  那么网关转发的时候，完整的路径为： /customer/customer/user/add
 *  b:如果有
 *  那么网关转发的时候，完整的路径为：/customer/user/add
 *  那么显然，我们希望的是第二种情况，所以就需要配置上StripPrefix=1
 *  StripPrefix可以接受一个非负整数，对应的具体实现是StripPrefixGatewayFilterFactory
 *  StripPrefix读经的整数即对应层数，如a，b，转发时会去掉/的第一层
 */
public class WebRequestRoutePrefix {
    public final static String ROUTE_PREFIX="";
}
