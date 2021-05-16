package com.lckjsoft.gateway.entity;


import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zgl
 */
public class GatewayPredicateDefinition {

    /** 断言对应的Name*/
    private String name;
    /** 配置的断言规则*/
    private Map<String, String> args = new LinkedHashMap<String,String>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public void setArgs(Map<String, String> args) {
        this.args = args;
    }
}