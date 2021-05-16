package com.lckjsoft.gateway.sentinel;


import com.alibaba.cloud.sentinel.annotation.SentinelRestTemplate;
import com.alibaba.csp.sentinel.datasource.Converter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @Author: 一个逗比程序员
 * @Project: lz-ac
 * @Description:
 * @Date: Created in    2021/5/16 14:46
 * @Modified By:
 * @Modified Date:      2021/5/16
 */

@Configuration
public class ServiceConfiguration {

    @Bean
    @SentinelRestTemplate(blockHandler = "handleException", blockHandlerClass = ExceptionUtil.class)
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RestTemplate restTemplate2() {
        return new RestTemplate();
    }

    @Bean
    public Converter myConverter() {
        return  new JsonFlowRuleListConverter();
    }
}

