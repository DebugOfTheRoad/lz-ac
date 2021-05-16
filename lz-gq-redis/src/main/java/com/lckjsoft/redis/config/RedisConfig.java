package com.lckjsoft.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Author: 一个逗比程序员
 * @Project: lz-gq
 * @Description:
 * @Date: Created in    2021/5/16 22:37
 * @Modified By:
 * @Modified Date:      2021/5/16
 * config demo:
 * spring:
 *   redis:
 *     host: 127.0.0.1
 *     port: 6379
 *     database: 1
 *     password: 123456
 *     timeout: 1000
 *     pool:
 *       max-idle: 10
 *       min-idle: 1
 *       max-active: 100
 *       max-wait: 10000
 */
@Configuration
@Slf4j
public class RedisConfig {

    @Value("${spring.redis.database}")
    private int database;

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.timeout:1000}")
    private int timeout;

    @Value("${spring.redis.pool.max-active:50}")
    private int maxActive;

    @Value("${spring.redis.pool.max-idle:10}")
    private int maxIdle;

    @Value("${spring.redis.pool.min-idle:1}")
    private int minIdle;

    @Value("${spring.redis.pool.max-wait:10000}")
    private long maxWaitMillis;

    @Bean
    public JedisPool jedisPool(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        jedisPoolConfig.setMaxTotal(maxActive);
        jedisPoolConfig.setMinIdle(minIdle);
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password, database);
        log.info( "JedisPool注入成功！");
        log.info( "redis地址：" + host + ":" + port);
        return  jedisPool;
    }

    @Bean
    public JedisPoolConfig redisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        jedisPoolConfig.setMaxTotal(maxActive);
        jedisPoolConfig.setMinIdle(minIdle);
        return jedisPoolConfig;
    }

}
