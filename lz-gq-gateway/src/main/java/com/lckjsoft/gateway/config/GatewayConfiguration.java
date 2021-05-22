package com.lckjsoft.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.lckjsoft.gateway.exception.GlobalErrorWebExceptionHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

/**
 * 网关的一些配置
 */
@Configuration
public class GatewayConfiguration {

    private final ServerProperties serverProperties;

    private final ApplicationContext applicationContext;

    private final ResourceProperties resourceProperties;

    private final List<ViewResolver> viewResolvers;

    private final ServerCodecConfigurer serverCodecConfigurer;

    public GatewayConfiguration(ServerProperties serverProperties,
                                ApplicationContext applicationContext,
                                ResourceProperties resourceProperties,
                                ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                ServerCodecConfigurer serverCodecConfigurer) {
        this.serverProperties = serverProperties;
        this.applicationContext = applicationContext;
        this.resourceProperties = resourceProperties;
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    /**
     * 网关集成限流熔断
     * 步骤
     * step 1，
     *     引入依赖：sentinel-spring-cloud-gateway-adapter
     * step 2，
     *     配置：
     *     spring.cloud
     *     #sentinel dashboard管理后台
     *     sentinel:
     *       eager: true
     *       transport:
     *         dashboard: 10.192.168.100:8880
     *         port: 4567   #此端口是gateway与sentinel dashboard通信使用，随机未占用端口
     * step 3，
     *     注入全局filter sentinelGatewayFilter，
     *     处理异常
     *        网关返回：SentinelGatewayBlockExceptionHandler
     *        服务返回：JSON。。。。。
     * @return
     */
    @Bean
    @Order(-1)
    public GlobalFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter();
    }

    /**
     * sentinel 异常/消息处理
     * @param myBlockRequestHandler
     * @return
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler(BlockRequestHandler myBlockRequestHandler) {
        // Register the block exception handler for Spring Cloud Gateway.

        //重定向bloack处理
        //GatewayCallbackManager.setBlockHandler(new RedirectBlockRequestHandler("http://www.baidu.com"));

        //自定义bloack处理
        GatewayCallbackManager.setBlockHandler(myBlockRequestHandler);

        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }


    /**
     * 限流 资源 巴拉巴拉
     * @return
     */
    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }

    /**
     * 自定义的BlockRequestHandler
     *
     * @return
     */
    @Bean(name = "myBlockRequestHandler")
    public BlockRequestHandler myBlockRequestHandler() {
        BlockRequestHandler blockRequestHandler = new BlockRequestHandler() {
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
                return ServerResponse.status(HttpStatus.BAD_GATEWAY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue("老铁，你手速太快了" + throwable.getClass()));
            }
        };
        return blockRequestHandler;
    }

    /**
     * 全局错误处理
     * @param errorAttributes
     * @return
     */
    @Bean("myErrorWebExceptionHandler")
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public GlobalErrorWebExceptionHandler myErrorWebExceptionHandler(ErrorAttributes errorAttributes) {

        GlobalErrorWebExceptionHandler exceptionHandler = new GlobalErrorWebExceptionHandler(
                errorAttributes,
                this.resourceProperties,
                this.serverProperties.getError(),
                this.applicationContext);

        exceptionHandler.setViewResolvers(this.viewResolvers);
        exceptionHandler.setMessageWriters(this.serverCodecConfigurer.getWriters());
        exceptionHandler.setMessageReaders(this.serverCodecConfigurer.getReaders());
        return exceptionHandler;
    }

//    @Bean
//    public JedisService jedisService(){
//        return new JedisService();
//    }
}
