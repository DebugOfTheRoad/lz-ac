package com.lckjsoft.redis.service;

import com.lckjsoft.common.constant.ConstantUtil;
import com.lckjsoft.common.util.NullUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.Transaction;

import java.util.*;

/**
 * @Author: 一个逗比程序员
 * @Project: lz-gq
 * @Description:
 * @Date: Created in    2021/5/16 22:41
 * @Modified By:
 * @Modified Date:      2021/5/16
 */
//@Slf4j
@Service
public class JedisService {

    @Autowired
    private JedisPool jedisPool;
    @Value("${spring.redis.database:1}")
    private int datasource;

    /**
     * 从jedis连接池中获取获取jedis对象
     * @return Jedis
     */
    public final Jedis getJedis(int datasource){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(datasource);
        } catch (Exception e) {
            returnResource(jedis);//销毁对象
        }
        return jedis;
    }

    public final Jedis getJedis(){
        return getJedis(datasource);
    }

    /**
     * 回收jedis
     * @param jedis
     */
    public void returnResource(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    /**
     * 关闭事务
     * @param transaction
     */
    public void closeTransaction(Transaction transaction) {

        if(transaction!=null){
            transaction.clear();
            transaction.close();
        }
    }

    /**
     * 设置过期时间
     * @param key
     * @param seconds 以秒为单位
     */
    public boolean setTimeout(String key, int seconds){
        Jedis jedis = null;
        try {
            if(NullUtil.isNotNull(key) && seconds>=0){
                jedis = getJedis();
                if(jedis!=null){
                    if(jedis.exists(key)){
                        if(seconds>0) {
                            jedis.expire(key, seconds);
                        } else if(seconds==0) {
                            jedis.expire(key, ConstantUtil.CACHE_TIMEOUT_DEFAULT);
                        }
                    }else if(jedis.exists(key.getBytes(ConstantUtil.UTF8))){
                        if(seconds>0) {
                            jedis.expire(key.getBytes(ConstantUtil.UTF8), seconds);
                        } else if(seconds==0) {
                            jedis.expire(key.getBytes(ConstantUtil.UTF8), ConstantUtil.CACHE_TIMEOUT_DEFAULT);
                        }
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.printf("redis setTimeout key["+key+"]exception : ",e);
        } finally{
            returnResource(jedis);
        }
        return false;
    }

    /**
     * 设置过期时间
     * @param keys
     * @param seconds 以秒为单位
     */
    public boolean setTimeout(String[] keys, int seconds){
        try {
            if(NullUtil.isNotNull(keys) && seconds>=0){
                for (String key : keys) {
                    setTimeout(key, seconds);
                }
                return true;
            }
        } catch (Exception e) {
            System.out.printf("redis setTimeout keys exception : ",e);
        }
        return false;
    }

    /**
     * 刷新所有缓存数据，慎重使用
     */
    public void flushAll(){
        flushDB(datasource);
    }

    /**
     * 刷新缓存数据，慎重使用
     */
    public void flushDB(int index){
        Jedis jedis = null;
        try {
            jedis = getJedis();
            if(jedis!=null){
                jedis.select(index);
                jedis.flushDB();
            }
        } finally{
            returnResource(jedis);
        }
    }


    /**
     * 添加字符串
     * @param key
     * @param value
     * @param seconds
     * @return
     */
    public boolean set(String key, Object value, int seconds){
        Jedis jedis = null;
        try {
            if(NullUtil.isNotNull(key) && NullUtil.isNotNull(value)){
                jedis = this.getJedis();
                if(jedis!=null){
                    if(seconds>0) {
                        jedis.setex(key.getBytes(ConstantUtil.UTF8), seconds, String.valueOf(value).getBytes(ConstantUtil.UTF8));
                    } else if(seconds==0) {
                        jedis.setex(key.getBytes(ConstantUtil.UTF8), ConstantUtil.CACHE_TIMEOUT_DEFAULT, String.valueOf(value).getBytes(ConstantUtil.UTF8));
                    } else {
                        jedis.set(key.getBytes(ConstantUtil.UTF8), String.valueOf(value).getBytes(ConstantUtil.UTF8));
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.printf("redis set key["+key+"]exception: ",e);
        } finally{
            this.returnResource(jedis);
        }
        return false;
    }

    /**永久*/
    public boolean setInfinite(String key, Object value){
        return set(key, value, -1);
    }

    /**默认*/
    public boolean setDefault(String key, Object value){
        return set(key, value, 0);
    }

    /**
     * 添加字符串
     * @param key
     * @param value
     * @param seconds
     * @return
     */
    public byte[] set(byte[] key, byte[] value, int seconds){
        Jedis jedis = null;
        try {
            if(NullUtil.isNotNull(key) && NullUtil.isNotNull(value)){
                jedis = this.getJedis();
                if(jedis!=null){
                    if(seconds>0) {
                        jedis.setex(key, seconds, value);
                    } else if(seconds==0) {
                        jedis.setex(key, ConstantUtil.CACHE_TIMEOUT_DEFAULT, value);
                    } else {
                        jedis.set(key, value);
                    }
                }
            }
        } catch (Exception e) {
            System.out.printf("redis set key byte exception: ",e);
        } finally{
            this.returnResource(jedis);
        }
        return value;
    }

    /**永久*/
    public byte[] setInfinite(byte[] key, byte[] value){
        return set(key, value, -1);
    }

    /**默认*/
    public byte[] setDefault(byte[] key, byte[] value){
        return set(key, value, 0);
    }

    /**
     * 添加队列数据
     * @param key
     * @param value
     * @return
     */
    public boolean lpush(String key, String value){
        Jedis jedis = null;
        try {
            if(NullUtil.isNotNull(key) && NullUtil.isNotNull(value)){
                jedis = this.getJedis();
                if(jedis!=null){
                    jedis.lpush(key.getBytes(ConstantUtil.UTF8), value.getBytes(ConstantUtil.UTF8));
                    return true;
                }
            }
        }catch (Exception e) {
            System.out.printf("redis lpush key["+key+"]exception : ",e);
        } finally{
            this.returnResource(jedis);
        }
        return false;
    }

    /**
     * 发布订阅消息
     * @param key
     * @param value
     */
    public boolean publish(String key, String value){
        Jedis jedis = null;
        try {
            if(NullUtil.isNotNull(key) && NullUtil.isNotNull(value)){
                jedis = this.getJedis();
                if(jedis!=null){
                    jedis.publish(key.getBytes(ConstantUtil.UTF8), value.getBytes(ConstantUtil.UTF8));
                    return true;
                }
            }
        }catch (Exception e) {
            System.out.printf("redis publish key["+key+"]exception : ", e);
        } finally{
            this.returnResource(jedis);
        }
        return false;
    }

    /**
     * 订阅消息
     */
    public boolean subscribe(JedisPubSub listener, String key){
        Jedis jedis = null;
        try {
            jedis = this.getJedis();
            if(jedis!=null){
                jedis.subscribe(listener, key);
                return true;
            }
        }catch (Exception e) {
            System.out.printf("redis subscribe key["+key+"]exception : ",e);
        } finally{
            this.returnResource(jedis);
        }
        return false;
    }


    /**
     * 添加string
     * @param key
     * @param field
     * @param value
     * @return
     */
    public boolean hset(String key, String field, String value){
        Jedis jedis = null;
        try {
            if(NullUtil.isNotNull(key) && NullUtil.isNotNull(field) && NullUtil.isNotNull(value)){
                jedis = this.getJedis();
                if(jedis!=null){
                    jedis.hset(key.getBytes(ConstantUtil.UTF8), field.getBytes(ConstantUtil.UTF8), value.getBytes(ConstantUtil.UTF8));
                    return true;
                }
            }
        }catch (Exception e) {
            System.out.printf("redis hset key["+key+"]exception : ",e);
        } finally{
            this.returnResource(jedis);
        }
        return false;
    }

    /**
     * 移除zadd添加的数据
     * @param key
     * @param member
     * @return
     */
    public boolean zremove(String key, String member) {
        Jedis jedis = null;
        try {
            if(NullUtil.isNotNull(key) && NullUtil.isNotNull(member)){
                jedis = this.getJedis();
                if(jedis!=null){
                    jedis.zrem(key.getBytes(ConstantUtil.UTF8), member.getBytes(ConstantUtil.UTF8));
                    return true;
                }
            }
        }catch (Exception e) {
            System.out.printf("redis zremove key["+key+"]exception : ",e);
        } finally{
            this.returnResource(jedis);
        }
        return false;
    }

    /**
     * Redis Zadd 命令用于将一个或多个成员元素及其分数值加入到有序集当中。
     如果某个成员已经是有序集的成员，那么更新这个成员的分数值，并通过重新插入这个成员元素，来保证该成员在正确的位置上。
     分数值可以是整数值或双精度浮点数。
     如果有序集合 key 不存在，则创建一个空的有序集并执行 ZADD 操作。
     当 key 存在但不是有序集类型时，返回一个错误。
     * @param key 唯一标识，用于查询对象的id集
     * @param score 排序值
     * @param member id值
     */
    public boolean zadd(String key, double score, String member) {
        Jedis jedis = null;
        try {
            if(NullUtil.isNotNull(key) && NullUtil.isNotNull(member)){
                jedis = this.getJedis();
                if(jedis!=null){
                    jedis.zadd(key.getBytes(ConstantUtil.UTF8), score, member.getBytes(ConstantUtil.UTF8));
                    return true;
                }
            }
        }catch (Exception e) {
            System.out.printf("redis zadd key["+key+"]exception : ",e);
        } finally{
            this.returnResource(jedis);
        }
        return false;
    }

    /**
     * 删除key数据
     * @param key
     */
    public void del(String key){
        Jedis jedis = null;
        try {
            if(NullUtil.isNotNull(key)){
                jedis = this.getJedis();
                if(jedis!=null){
                    if(jedis.exists(key)){
                        jedis.del(key);
                    }
                    if(jedis.exists(key.getBytes(ConstantUtil.UTF8))){
                        jedis.del(key.getBytes(ConstantUtil.UTF8));
                    }
                }
            }
        }catch (Exception e) {
            System.out.printf("redis del key["+key+"] exception:",e);
        } finally{
            this.returnResource(jedis);
        }
    }

    /**
     * 删除key数据
     * @param key
     */
    public void del(byte[] key){
        Jedis jedis = null;
        try {
            if(NullUtil.isNotNull(key)){
                jedis = this.getJedis();
                if(jedis!=null){
                    if(jedis.exists(key)){
                        jedis.del(key);
                    }
                }
            }
        }catch (Exception e) {
            System.out.printf("redis del key byte exception", e);
        } finally{
            this.returnResource(jedis);
        }
    }

    /**
     * 删除key前缀的所有key集合
     * @param key
     */
    public void delKeys(String key){
        Jedis jedis = null;
        try {
            if(NullUtil.isNotNull(key)){
                jedis = this.getJedis();
                if(jedis!=null){
                    Set<byte[]> keys = jedis.keys((key+"*").getBytes(ConstantUtil.UTF8));
                    if (NullUtil.isNotNull(keys)) {
                        Iterator<byte[]> its = keys.iterator();
                        while (its.hasNext()) {
                            byte[] keybyte = its.next();
                            if(jedis.exists(keybyte)){
                                jedis.del(keybyte);
                            }
                            String keyStr = new String(keybyte, ConstantUtil.UTF8);
                            if(jedis.exists(keyStr)){
                                jedis.del(keyStr);
                            }
                        }
                        keys.clear();
                    }
                }
            }
        }catch (Exception e) {
            System.out.printf("redis del key["+key+"] exception:",e);
        } finally{
            this.returnResource(jedis);
        }
    }

    /**
     * 按照传入的key进行查询，保持key不变，不处理<br/>
     * 模糊查询key值 h?llo will match hello hallo hhllo h*llo will match hllo<br/>
     * heeeello h[ae]llo will match hello and hallo, but not hillo<br/>
     * @param key
     */
    public Set<String> keys(String key) {
        Jedis jedis = null;
        try {
            if (NullUtil.isNotNull(key)) {
                jedis = this.getJedis();
                if (jedis != null) {
                    Set<byte[]> sets = jedis.keys(key.getBytes(ConstantUtil.UTF8));
                    if (NullUtil.isNotNull(sets)) {
                        Set<String> results = new HashSet<String>();
                        Iterator<byte[]> its = sets.iterator();
                        while (its.hasNext()) {
                            byte[] value = its.next();
                            results.add(new String(value, ConstantUtil.UTF8));
                        }
                        return results;
                    }
                }
            }
        } catch (Exception e) {
            System.out.printf("redis keys exception : ", e);
        } finally {
            this.returnResource(jedis);
        }
        return null;
    }

    /**
     * 按照传入的key进行查询，保持key不变，不处理<br/>
     * 模糊查询key值 h?llo will match hello hallo hhllo h*llo will match hllo<br/>
     * heeeello h[ae]llo will match hello and hallo, but not hillo<br/>
     * @param key
     */
    public Set<byte[]> keys(byte[] key) {
        Jedis jedis = null;
        try {
            if (NullUtil.isNotNull(key)) {
                jedis = this.getJedis();
                if (jedis != null) {
                    return jedis.keys(key);
                }
            }
        } catch (Exception e) {
            System.out.printf("redis keysByte exception : ", e);
        } finally {
            this.returnResource(jedis);
        }
        return null;
    }

    /**
     * 获取key的总记录数
     *
     * @param key
     * @return
     */
    public long zcount(String key) {
        Jedis jedis = null;
        try {
            if (NullUtil.isNotNull(key)) {
                jedis = this.getJedis();
                if (jedis != null) {
                    return jedis.zcount(key.getBytes(ConstantUtil.UTF8), 0, Long.MAX_VALUE);// 总数
                }
            }
        } catch (Exception e) {
            System.out.printf("redis zcount exception : ", e);
        } finally {
            this.returnResource(jedis);
        }
        return 0;
    }


    /**
     * 获取key存储的值
     * @param key
     * @return
     */
    public String get(String key) {
        Jedis jedis = null;
        try {
            if (NullUtil.isNotNull(key)) {
                jedis = this.getJedis();
                if (jedis != null && jedis.exists(key.getBytes(ConstantUtil.UTF8))) {
                    byte[] value = jedis.get(key.getBytes(ConstantUtil.UTF8));
                    if(value!=null&&value.length>0) {
                        return new String(value, ConstantUtil.UTF8);
                    }
                }
            }
        } catch (Exception e) {
            System.out.printf("redis get exception : ", e);
        } finally {
            this.returnResource(jedis);
        }
        return null;
    }

    /**
     * 获取key存储的值
     * @param key
     * @return
     */
    public byte[] get(byte[] key) {
        Jedis jedis = null;
        try {
            if (NullUtil.isNotNull(key)) {
                jedis = this.getJedis();
                if (jedis != null && jedis.exists(key)) {
                    return jedis.get(key);
                }
            }
        } catch (Exception e) {
            System.out.printf("redis get byte exception : ", e);
        } finally {
            this.returnResource(jedis);
        }
        return null;
    }

    /**
     * 取出队列，不会进入等待状态
     *
     * @param key
     * @return
     */
    public String rpop(String key) {
        Jedis jedis = null;
        try {
            if (NullUtil.isNotNull(key)) {
                jedis = this.getJedis();
                if (jedis != null) {
                    byte[] value = jedis.rpop(key.getBytes(ConstantUtil.UTF8));
                    if(value!=null){
                        return new String(value, ConstantUtil.UTF8);
                    }
                }
            }
        } catch (Exception e) {
            System.out.printf("redis rpop exception : ", e);
        } finally {
            this.returnResource(jedis);
        }
        return null;
    }

    /**
     * 取出队列数据，如果没有数据会进入等待一直到有数据
     *
     * @param key
     * @return
     */
    public List<String> brpop(String key) {
        Jedis jedis = null;
        try {
            if (NullUtil.isNotNull(key)) {
                jedis = this.getJedis();
                if (jedis != null) {
                    List<byte[]> list = jedis.brpop(0, key.getBytes(ConstantUtil.UTF8));
                    if (NullUtil.isNotNull(list)) {
                        List<String> results = new ArrayList<String>(0);
                        for (byte[] value : list) {
                            results.add(new String(value, ConstantUtil.UTF8));
                        }
                        return results;
                    }
                }
            }
        } catch (Exception e) {
            System.out.printf("redis brpop exception : ", e);
        } finally {
            this.returnResource(jedis);
        }
        return null;
    }

    /**
     * 获取查询的字段值
     *
     * @param key
     * @param field
     * @return
     */
    public String hget(String key, String field) {
        Jedis jedis = null;
        try {
            if (NullUtil.isNotNull(key) && NullUtil.isNotNull(field)) {
                jedis = this.getJedis();
                if (jedis != null) {
                    return new String(jedis.hget(key.getBytes(ConstantUtil.UTF8), field.getBytes(ConstantUtil.UTF8)),
                            ConstantUtil.UTF8);
                }
            }
        } catch (Exception e) {
            System.out.printf("redis hget exception : ", e);
        } finally {
            this.returnResource(jedis);
        }
        return null;
    }


    /**返回当前选定数据库中的key数量。*/
    public Long getDBSize() {
        Jedis jedis = null;
        try {
            jedis = this.getJedis();
            if (jedis != null) {
                return jedis.dbSize();
            }
        } finally {
            this.returnResource(jedis);
        }
        return 0L;
    }

    /**
     * 根据存储类型，使用不同的方式获取存储的值
     * @param key
     * @param field
     * @return
     */
    public byte[] getKeyValue(Jedis jedis, String key, String field) {
        try {
            if (jedis != null && NullUtil.isNotNull(key)) {
                String type = jedis.type(key.getBytes(ConstantUtil.UTF8));
                System.out.printf("redis key[" + key + "]'s type is : " + type);
                if ("hash".equals(type)) {
                    // hset
                    return jedis.hget(key.getBytes(ConstantUtil.UTF8), field.getBytes(ConstantUtil.UTF8));
                } else if ("zset".equals(type)) {
                    // zadd有序集合
                    Set<byte[]> idSet = jedis.zrangeByScore(key.getBytes(ConstantUtil.UTF8), "-inf".getBytes(ConstantUtil.UTF8), "+inf".getBytes(ConstantUtil.UTF8));
                    if (idSet != null && idSet.size() == 1) {
                        return idSet.iterator().next();
                    }
                } else if ("set".equals(type)) {
                    // sadd集合
                    Set<byte[]> idSet = jedis.smembers(key.getBytes(ConstantUtil.UTF8));
                    if (idSet != null && idSet.size() == 1) {
                        return idSet.iterator().next();
                    }
                } else if ("list".equals(type)) {
                    // lpush队列
                    return jedis.lpop(key.getBytes(ConstantUtil.UTF8));
                } else if ("string".equals(type)) {
                    // set字符串
                    return jedis.get(key.getBytes(ConstantUtil.UTF8));
                }
            }
        } catch (Exception e) {
            System.out.printf("redis getKeyValue exception : ", e);
        }
        return null;
    }
}
