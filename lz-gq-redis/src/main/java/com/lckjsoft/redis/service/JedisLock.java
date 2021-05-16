package com.lckjsoft.redis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 一个逗比程序员
 * @Project: lz-gq
 * @Description:
 * @Date: Created in    2021/5/16 22:39
 * @Modified By:
 * @Modified Date:      2021/5/16
 */
@Component
@Slf4j
public class JedisLock {

    @Autowired
    private JedisService jedisService;
    private Thread currentThread;
    private boolean locked = false;
    private String lockKey;
    /** 过期时间，单位：秒 */
    private int expires;

    public JedisLock(Object lockKey) {
        this.lockKey = "lock:" + lockKey;
        /** 释放时间不宜过长，否则引起线程堵塞 */
        this.expires = 60;
    }

    /**
     * @param lockKey
     *            锁
     * @param expires
     * 超时时间，单位：秒
     */
    public JedisLock(Object lockKey, int expires) {
        this.lockKey = "lock:" + lockKey;
        this.expires = expires;
    }

    private static Map<String, JedisLock> lockMap = new HashMap<String, JedisLock>();
    public static JedisLock getLock(String lockkey){
        if(!lockMap.containsKey(lockkey)){
            lockMap.put(lockkey, new JedisLock(lockkey));
        }
        return lockMap.get(lockkey);
    }

    public boolean lock() {
        Jedis jedis = null;
        try {
            //获取连接时间不宜过长
            int timeout = 10;
            jedis = jedisService.getJedis();
            while (timeout > 0) {
                long nowTime = System.currentTimeMillis();
                String expiresStr = String.valueOf(nowTime + expires * 1000);
                if (jedis.setnx(lockKey, expiresStr) == 1) {
                    jedis.expire(lockKey, expires);
                    locked = true;
                    currentThread = Thread.currentThread();
                    log.info("JedisLock.lock------->获取redis分布式同步锁。线程id："+currentThread.getId()+"，线程名："+currentThread.getName());
                    return true;
                }
                // redis里的时间
                String currentValueStr = jedis.get(lockKey);
                if (currentValueStr != null && Long.parseLong(currentValueStr) < System.currentTimeMillis()) {
                    // 表明已经超时了，原来的线程可能可能出现意外未能及时释放锁
                    String oldValueStr = jedis.getSet(lockKey, expiresStr);
                    // 为什么会有下面这个判断呢？因为多线程情况下可能同时有多个线程在这一时刻发现锁过期，那么就会同时执行getSet获取锁操作，
                    // 通过下面的比较，可以找到第一个执行getSet操作的线程，让其获得锁，其它的线程则重试
                    if (currentValueStr.equals(oldValueStr)) {
                        jedis.expire(lockKey, expires);
                        locked = true;
                        currentThread = Thread.currentThread();
                        log.info("JedisLock.lock------->获取redis分布式同步锁。线程id："+currentThread.getId()+"，线程名："+currentThread.getName());
                        return true;
                    }
                }
                timeout -= 100;
                Thread.sleep(100);
            }
        } catch (Exception e) {
            log.error("JedisLock.lock------->redis上锁异常，"+e.getMessage());
        } finally {
            jedisService.returnResource(jedis);
        }
        return false;
    }

    public void unlock() {
        Jedis jedis = null;
        try {
            if (currentThread != Thread.currentThread()) {
                return;
            }
            jedis = jedisService.getJedis();
            jedis.del(lockKey);
            locked = false;
            log.info("JedisLock.unlock------->释放redis分布式同步锁。线程id："+currentThread.getId()+"，线程名："+currentThread.getName());
            currentThread = null;
        } catch (Exception e) {
            log.error("JedisLock.unlock------->redis解锁异常，"+ e.getMessage());
        } finally {
            jedisService.returnResource(jedis);
        }
    }

    public boolean isLocked() {
        return locked;
    }
}
