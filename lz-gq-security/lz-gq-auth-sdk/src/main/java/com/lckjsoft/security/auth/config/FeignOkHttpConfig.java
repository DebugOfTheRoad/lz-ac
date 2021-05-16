package com.lckjsoft.security.auth.config;

import com.lckjsoft.security.auth.interceptor.OkHttpTokenInterceptor;
import feign.Feign;
import okhttp3.ConnectionPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @Author: 一个逗比程序员
 * @Project: lz-gq
 * @Description:
 * @Date: Created in    2021/5/16 23:13
 * @Modified By:
 * @Modified Date:      2021/5/16
 */

@AutoConfigureBefore(FeignAutoConfiguration.class)
@Configuration
@ConditionalOnClass(Feign.class)
public class FeignOkHttpConfig {

    @Autowired
    OkHttpTokenInterceptor okHttpLoggingInterceptor;

    private int feignOkHttpReadTimeout = 60;
    private int feignConnectTimeout = 60;
    private int feignWriteTimeout = 120;

    @Bean
    public okhttp3.OkHttpClient okHttpClient() {
        return new okhttp3.OkHttpClient.Builder().readTimeout(feignOkHttpReadTimeout, TimeUnit.SECONDS).connectTimeout(feignConnectTimeout, TimeUnit.SECONDS)
                .writeTimeout(feignWriteTimeout, TimeUnit.SECONDS).connectionPool(new ConnectionPool())
                .addInterceptor(okHttpLoggingInterceptor)
                .build();
    }
}
