package com.lckjsoft.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

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
}
