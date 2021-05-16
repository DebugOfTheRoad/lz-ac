package com.lckjsoft.gateway.entity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author uid40330
 */
public class GatewayFilterDefinition {
    /** Filter Name*/
    private String name;
    /** 对应的路由规则*/
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

    @Override
    public String toString() {
        return "GatewayFilterDefinition{" +
                "name='" + name + '\'' +
                ", args=" + args +
                '}';
    }
}
