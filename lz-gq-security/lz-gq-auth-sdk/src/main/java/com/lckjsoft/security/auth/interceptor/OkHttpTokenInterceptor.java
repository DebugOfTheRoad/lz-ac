package com.lckjsoft.security.auth.interceptor;

import com.lckjsoft.common.context.BaseContextHandler;
import com.lckjsoft.security.auth.config.UserAuthConfig;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Author: 一个逗比程序员
 * @Project: lz-gq
 * @Description:
 * @Date: Created in    2021/5/16 23:15
 * @Modified By:
 * @Modified Date:      2021/5/16
 */

@Component
public class OkHttpTokenInterceptor implements Interceptor {
    @Autowired
    @Lazy
    private UserAuthConfig userAuthConfig;


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request newRequest = chain.request()
                .newBuilder()
                .header(userAuthConfig.getTokenHeader(), BaseContextHandler.getToken())
                .build();

        Response response = chain.proceed(newRequest);
//        if (HttpStatus.FORBIDDEN.value() == response.code()) {
//            if (response.body().string().contains(String.valueOf(CommonConstants.EX_CLIENT_INVALID_CODE))) {
//                log.info("Client Token Expire,Retry to request...");
//                serviceAuthUtil.refreshClientToken();
//                newRequest = chain.request()
//                        .newBuilder()
//                        .header(userAuthConfig.getTokenHeader(), BaseContextHandler.getToken())
//                        .header(serviceAuthConfig.getTokenHeader(), serviceAuthUtil.getClientToken())
//                        .build();
//                response = chain.proceed(newRequest);
//            }
//        }
        return response;
    }
}
