package com.lckjsoft.gateway.sentinel;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;

/**
 * @Author: 一个逗比程序员
 * @Project: lz-ac
 * @Description:
 * @Date: Created in    2021/5/16 14:46
 * @Modified By:
 * @Modified Date:      2021/5/16
 */
public class JsonFlowRuleListConverter implements Converter<String, List<FlowRule>> {
    @Override
    public List<FlowRule> convert(String source) {
        return JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
        });
    }
}
