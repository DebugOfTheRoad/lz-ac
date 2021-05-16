package com.lckjsoft.gateway.sentinel;

import com.alibaba.cloud.sentinel.rest.SentinelClientHttpResponse;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;

/**
 * @Author: 一个逗比程序员
 * @Project: lz-ac
 * @Description:
 * @Date: Created in    2021/5/16 14:46
 * @Modified By:
 * @Modified Date:      2021/5/16
 */
public class ExceptionUtil {

    public static SentinelClientHttpResponse handleException(HttpRequest request,
                                                             byte[] body, ClientHttpRequestExecution execution, BlockException ex) {
        System.out.println("Oops: " + ex.getClass().getCanonicalName());
        return new SentinelClientHttpResponse("custom block info");
    }

}
