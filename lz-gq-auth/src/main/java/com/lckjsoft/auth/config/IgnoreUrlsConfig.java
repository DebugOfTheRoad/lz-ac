package com.lckjsoft.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
/**
 * 网关白名单配置
 * @author uid40330
 */
@Component
@ConfigurationProperties(prefix="oauth2.security")
public class IgnoreUrlsConfig {

    private List<String> ignoreurls;

    public List<String> getIgnoreurls() {
        return ignoreurls;
    }

    public void setIgnoreurls(List<String> ignoreurls) {
        this.ignoreurls = ignoreurls;
    }

    @Override
    public String toString() {
        return "IgnoreUrlsConfig{" +
                "ignoreurls=" + ignoreurls +
                '}';
    }
}
