package com.hanhai.cloud.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 提供了对redis中 value list hash 和set的简单封装，所有操作绑定前缀和指定默认时间，使用需要让工具类继承本类，构造方法中设置前缀与默认时间即可。
 *
 * @author wmgx
 * @create 2021-05-16-22:38
 **/
public abstract class RedisBaseUtils {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 前缀
     */
    private String prefix;
    /**
     * 过期时间 单位秒 默认半个小时
     */
    private long defaultExpire = 30 * 60;


    public String getPrefix() {
        return prefix.substring(0, prefix.length() - 2);
    }

    public long getDefaultExpire() {
        return defaultExpire;
    }

    public String initKeyWithPrefix(String key) {
        return prefix + key;
    }

    /**
     * 设置指定前缀的构造方法
     *
     * @param prefix
     *         前缀
     */
    public RedisBaseUtils(String prefix) {
        this.prefix = prefix + ":";
    }

    /**
     * 设置指定前缀和默认过期时间的方法
     *
     * @param prefix
     *         前缀
     * @param defaultExpire
     *         默认过期时间
     */
    public RedisBaseUtils(String prefix, long defaultExpire) {
        this.prefix = prefix + ":";
        this.defaultExpire = defaultExpire;
    }

    /**
     * 获取value操作对象，对于未封装的方法，可以手动操作。
     *
     * @return 操作对象
     */
    public ValueOperations<String, Object> getValueOperations() {
        return redisTemplate.opsForValue();
    }

    /**
     * 添加一个值
     *
     * @param key
     *         key
     * @param value
     *         值
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(prefix + key, value, defaultExpire, TimeUnit.SECONDS);
    }

    /**
     * 添加一个对象并且设置过期时间 单位秒
     *
     * @param key
     *         key
     * @param value
     *         值
     * @param expire
     *         过期时间
     */
    public void set(String key, Object value, long expire) {
        redisTemplate.opsForValue().set(prefix + key, value, expire, TimeUnit.SECONDS);
    }

    /**
     * 根据key删除一个键值对
     *
     * @param key
     *         key
     *
     * @return 删除是否成功
     */

    public Boolean delete(String key) {
        return redisTemplate.delete(prefix + key);
    }

    /**
     * 根据key获取一个值
     *
     * @param key
     *         key
     *
     * @return 获取的对象
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(prefix + key);
    }

    /**
     * 重新设置指定key的过期时间 单位秒
     *
     * @param key
     *         key
     * @param time
     *         过期时间单位秒
     *
     * @return 设置是否成功
     */
    public Boolean expire(String key, long time) {
        return redisTemplate.expire(prefix + key, time, TimeUnit.SECONDS);
    }

    /**
     * 获取某个key的剩余过期时间单位秒
     *
     * @param key
     *         key
     *
     * @return 过期时间，单位秒
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(prefix + key, TimeUnit.SECONDS);
    }

    /**
     * 获取某个key的剩余过期时间单位自己指定
     *
     * @param key
     *         key
     * @param timeUnit
     *         时间单位
     *
     * @return 过期时间
     */
    public Long getExpire(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(prefix + key, timeUnit);
    }

    /**
     * 自增某个值按照1
     *
     * @param key
     *         key
     *
     * @return
     */
    public Long incr(String key) {
        return redisTemplate.opsForValue().increment(prefix + key);
    }


    /**
     * 自增某个值按照指定自增量
     *
     * @param key
     *         key
     * @param delta
     *         自增量
     *
     * @return
     */
    public Long incr(String key, int delta) {
        return redisTemplate.opsForValue().increment(prefix + key, delta);
    }

    /**
     * 获取指定key的大小
     *
     * @param key
     *         key
     *
     * @return 大小
     */
    public Long valueSize(String key) {
        return redisTemplate.opsForValue().size(prefix + key);
    }

    /**
     * 获取list操作对象
     *
     * @return list操作对象
     */
    public ListOperations<String, Object> getListOperations() {
        return redisTemplate.opsForList();
    }

    /**
     * 获取list的一个操作对象，每次不用都指定key
     *
     * @param key
     *
     * @return
     */
    public BoundListOperations<String, Object> getBoundListOps(String key) {
        return redisTemplate.boundListOps(prefix + key);
    }

    /**
     * 获取list中的某一段
     *
     * @param key
     *         list的key
     * @param start
     *         开始位置
     * @param end
     *         结束位置
     *
     * @return 值
     */
    public List<Object> range(String key, long start, long end) {
        return redisTemplate.opsForList().range(prefix + key, start, end);
    }

    /**
     * 修剪列表，使其只包含指定范围内的元素
     *
     * @param key
     *         key
     * @param start
     *         开始
     * @param end
     *         结束
     */
    public void trim(String key, long start, long end) {
        redisTemplate.opsForList().trim(prefix + key, start, end);
    }


    /**
     * 返回list的siee
     *
     * @param key
     *         key
     *
     * @return 大小
     */
    public Long listSize(String key) {
        return redisTemplate.opsForList().size(prefix + key);
    }


    /**
     * 从左侧添加一个元素
     *
     * @param key
     *         key
     * @param value
     *         元素
     *
     * @return
     */
    public Long leftPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(prefix + key, value);
    }

    /**
     * 左侧添加一堆元素
     *
     * @param key
     *         key
     * @param values
     *         一堆元素
     *
     * @return
     */
    public Long leftPushAll(String key, Object... values) {
        return redisTemplate.opsForList().leftPushAll(prefix + key, values);
    }


    /**
     * 左侧添加一堆元素
     *
     * @param key
     *         key
     * @param values
     *         一堆元素
     *
     * @return
     */
    public Long leftPushAll(String key, Collection<Object> values) {
        return redisTemplate.opsForList().leftPushAll(prefix + key, values);
    }

    /**
     * 仅当列表存在时，才在键之前添加值。
     *
     * @param key
     *         key
     * @param value
     *         值
     *
     * @return
     */

    public Long leftPushIfPresent(String key, Object value) {
        return redisTemplate.opsForList().leftPushIfPresent(prefix + key, value);
    }


    /**
     * 将值插入到指定结点之前。
     *
     * @param key
     *         key
     * @param pivot
     *         指定结点
     * @param value
     *         值
     *
     * @return
     */
    public Long leftPush(String key, Object pivot, Object value) {
        return redisTemplate.opsForList().leftPush(prefix + key, pivot, value);
    }

    /**
     * 从右侧添加一个元素
     *
     * @param key
     *         key
     * @param value
     *         元素
     *
     * @return
     */
    public Long rightPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(prefix + key, value);
    }

    /**
     * 从右侧添加一堆元素
     *
     * @param key
     *         key
     * @param values
     *         一堆元素
     *
     * @return
     */
    public Long rightPushAll(String key, Object... values) {
        return redisTemplate.opsForList().leftPushIfPresent(prefix + key, values);
    }

    /**
     * 从右侧添加一堆元素
     *
     * @param key
     *         key
     * @param values
     *         一堆元素
     *
     * @return
     */
    public Long rightPushAll(String key, Collection<Object> values) {
        return redisTemplate.opsForList().leftPushIfPresent(prefix + key, values);
    }

    /**
     * 仅当列表存在时，才在键之后添加值。
     *
     * @param key
     *         key
     * @param value
     *         一堆元素
     *
     * @return
     */
    public Long rightPushIfPresent(String key, Object value) {
        return redisTemplate.opsForList().rightPushIfPresent(prefix + key, value);
    }

    /**
     * 将值插入到指定结点之后。
     *
     * @param key
     *         key
     * @param pivot
     *         指定结点
     * @param value
     *         值
     *
     * @return
     */
    public Long rightPush(String key, Object pivot, Object value) {
        return redisTemplate.opsForList().rightPush(prefix + key, pivot, value);
    }

    /**
     * 将指定的值设置到指定的下标。
     *
     * @param key
     *         key
     * @param index
     *         下标
     * @param value
     *         值
     *
     * @return
     */
    public void listSetValue(String key, long index, Object value) {
        redisTemplate.opsForList().set(prefix + key, index, value);
    }

    /**
     * 从键处存储的列表中删除值的第一个计数出现次数。
     *
     * @param key
     *         key
     * @param count
     *         次数
     * @param value
     *         值
     *
     * @return
     */
    public Long listRemove(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(prefix + key, count, value);
    }


    /**
     * 获取指定下标处的值
     *
     * @param key
     *         key
     * @param index
     *         下标
     *
     * @return 值
     */
    public Object listIndex(String key, long index) {
        return redisTemplate.opsForList().index(prefix + key, index);
    }


    /**
     * 弹出list中第一个
     *
     * @param key
     *         key
     *
     * @return
     */
    public Object leftPop(String key) {
        return redisTemplate.opsForList().leftPop(prefix + key, defaultExpire, TimeUnit.SECONDS);
    }


    /**
     * 从key处存储的列表中删除并返回第一个元素。 阻止连接，直到元素可用或超时。
     *
     * @param key
     *         key
     * @param timeout
     *         时间， 单位秒
     *
     * @return
     */
    public Object leftPop(String key, long timeout) {
        return redisTemplate.opsForList().leftPop(prefix + key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 弹出list中最后一个
     *
     * @param key
     *         key
     *
     * @return
     */
    public Object rightPop(String key) {
        return redisTemplate.opsForList().rightPop(prefix + key, defaultExpire, TimeUnit.SECONDS);
    }

    /**
     * 从key处存储的列表中删除并返回最后一个元素。 阻止连接，直到元素可用或超时。
     *
     * @param key
     *         key
     * @param timeout
     *         时间， 单位秒
     *
     * @return
     */
    public Object rightPop(String key, long timeout) {
        return redisTemplate.opsForList().rightPop(prefix + key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 返回hash操作对象
     *
     * @return hash操作对象
     */
    public HashOperations<String, Object, Object> getHashOperations() {
        return redisTemplate.opsForHash();
    }

    /**
     * 获取hash的绑定操作对象，每次操作不用指定key
     *
     * @param key
     *         key
     *
     * @return hash操作对象
     */
    public BoundHashOperations<String, Object, Object> getBoundHashOps(String key) {
        return redisTemplate.boundHashOps(prefix + key);
    }

    /**
     * 删除指定的key中的 hashkey们
     *
     * @param key
     *         key
     * @param hashKeys
     *         hashkey
     *
     * @return
     */
    public Long hashDelete(String key, Object... hashKeys) {
        return redisTemplate.opsForHash().delete(prefix + key, hashKeys);
    }

    /**
     * 删除指定的key中的 hashkey
     *
     * @param key
     *         key
     * @param hashKey
     *         hashkey
     *
     * @return
     */
    public Boolean hasKey(String key, Object hashKey) {
        return redisTemplate.opsForHash().hasKey(prefix + key, hashKey);
    }


    /**
     * 获取指定该key中的指定hash的值
     *
     * @param key
     *         key
     * @param hashKey
     *         hashkey
     *
     * @return 值
     */
    public Object hashGet(String key, Object hashKey) {
        return redisTemplate.opsForHash().get(prefix + key, hashKey);
    }


    /**
     * 获取指定key的多个hash对于的值
     *
     * @param key
     *         key
     * @param hashKeys
     *         多个hashkey
     *
     * @return 值
     */
    public List<Object> multiGet(String key, Collection<Object> hashKeys) {
        return redisTemplate.opsForHash().multiGet(prefix + key, hashKeys);
    }


    /**
     * 获取指定key的所有hashkey
     *
     * @param key
     *
     * @return
     */
    public Set<Object> keys(String key) {
        return redisTemplate.opsForHash().keys(prefix + key);
    }


    /**
     * 获取指定key的hash的长度
     *
     * @param key
     *         key
     * @param hashKey
     *         hash
     *
     * @return 擦汗高难度
     */
    public Long lengthOfValueForHash(String key, Object hashKey) {
        return redisTemplate.opsForHash().lengthOfValue(prefix + key, hashKey);
    }

    /**
     * 获取hash的size
     *
     * @param key
     *         key
     *
     * @return size
     */
    public Long sizeForHash(String key) {
        return redisTemplate.opsForHash().size(prefix + key);
    }


    /**
     * 将map中的所有的键值对添加到key中
     *
     * @param key
     *         key
     * @param m
     *         键值对
     */
    public void putAllForHash(String key, Map<? extends Object, ? extends Object> m) {
        redisTemplate.opsForHash().putAll(prefix + key, m);
    }

    /**
     * 将指定的value放到指定的key以指定的hashkey
     *
     * @param key
     *         key
     * @param hashKey
     *         hashkey
     * @param value
     *         value
     */
    public void put(String key, Object hashKey, Object value) {
        redisTemplate.opsForHash().put(prefix + key, hashKey, value);
    }


    /**
     * 将指定的key的所有value取出
     *
     * @param key
     *         key
     *
     * @return values
     */
    public List<Object> valuesForHash(String key) {
        return redisTemplate.opsForHash().values(prefix + key);
    }

    /**
     * 获取一个set操作对象
     *
     * @return set操作对象
     */
    public SetOperations<String, Object> getSetOperations() {
        return redisTemplate.opsForSet();
    }

    /**
     * 获取一个set操作对象绑定到key上
     *
     * @return set操作对象绑定到key
     */
    public BoundSetOperations<String, Object> getSetOperations(String key) {
        return redisTemplate.boundSetOps(prefix + key);
    }

    /**
     * 向set中添加一个元素
     *
     * @param key
     *         key
     * @param values
     *         元素
     *
     * @return
     */
    public Long addForSet(String key, Object... values) {
        return redisTemplate.opsForSet().add(prefix + key, values);
    }

    /**
     * 从set中删除一个元素
     *
     * @param key
     *         key
     * @param values
     *         元素
     *
     * @return
     */
    public Long removeFormSet(String key, Object... values) {
        return redisTemplate.opsForSet().remove(prefix + key, values);
    }

    /**
     * 从set中弹出一个元素
     *
     * @param key
     *         key
     *
     * @return 元素
     */
    public Object popFormSet(String key) {
        return redisTemplate.opsForSet().pop(prefix + key);
    }

    /**
     * 从set中弹出count个元素
     *
     * @param key
     *         key
     * @param count
     *         个数
     *
     * @return 元素
     */
    public List<Object> popFormSet(String key, long count) {
        return redisTemplate.opsForSet().pop(prefix + key, count);
    }


    /**
     * 将指定的value从指定的key中移动到指定的destKey中
     *
     * @param key
     *         源key
     * @param value
     *         值
     * @param destKey
     *         目标key
     *
     * @return
     */
    public Boolean moveForSet(String key, Object value, String destKey) {
        return redisTemplate.opsForSet().move(prefix + key, value, prefix + "::" + destKey);
    }

    /**
     * 获取kset的大小
     *
     * @param key
     *         key
     *
     * @return 大小
     */
    public Long sizeForSet(String key) {
        return redisTemplate.opsForSet().size(prefix + key);
    }


    /**
     * 判断元素是否在key中
     *
     * @param key
     *         key
     * @param o
     *         元素
     *
     * @return 是否
     */
    public Boolean isMember(String key, Object o) {
        return redisTemplate.opsForSet().isMember(prefix + key, o);
    }


    /**
     * 计算两个key的交集
     *
     * @param key
     *         key
     * @param otherKey
     *         key
     *
     * @return
     */
    public Set<Object> intersect(String key, String otherKey) {
        return redisTemplate.opsForSet().intersect(prefix + key, prefix + otherKey);
    }


    /**
     * 计算两个key的交际存储到第三个key中
     *
     * @param key
     *         key1
     * @param otherKey
     *         key2
     * @param destKey
     *         存储key
     *
     * @return
     */
    public Long intersectAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForSet().intersectAndStore(prefix + key, prefix + otherKey, prefix + destKey);
    }


    /**
     * 计算两个key的并集
     *
     * @param key
     *         key1
     * @param otherKey
     *         key2
     *
     * @return
     */
    public Set<Object> union(String key, String otherKey) {
        return redisTemplate.opsForSet().union(prefix + key, prefix + otherKey);
    }

    /**
     * 计算两个key的交集 存储到第三个key 中
     *
     * @param key
     *         key1
     * @param otherKey
     *         key2
     * @param destKey
     *         key3
     *
     * @return
     */

    public Long unionAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForSet().intersectAndStore(prefix + key, prefix + otherKey, prefix + destKey);
    }


    /**
     * 计算两个key的差集
     *
     * @param key
     *         key1
     * @param otherKey
     *         key2
     *
     * @return
     */
    public Set<Object> difference(String key, String otherKey) {
        return redisTemplate.opsForSet().difference(prefix + key, prefix + otherKey);
    }

    /**
     * 计算两个key的差集 存储到第三个key中
     *
     * @param key
     *         key1
     * @param otherKey
     *         key2
     * @param destKey
     *         key3
     *
     * @return
     */
    public Long differenceAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForSet().intersectAndStore(prefix + key, prefix + otherKey, prefix + destKey);
    }

    /**
     * 获取指定key的set中的所以元素
     *
     * @param key
     *         key
     *
     * @return
     */
    public Set<Object> members(String key) {
        return redisTemplate.opsForSet().members(prefix + key);
    }


    /**
     * 从指定的key中随机的获取一个元素
     *
     * @param key
     *
     * @return
     */
    public Object randomMember(String key) {
        return redisTemplate.opsForSet().randomMember(prefix + key);
    }


    /**
     * 从指定的可以中随机获取count个不同的元素
     *
     * @param key
     *         key
     * @param count
     *         个数
     *
     * @return
     */
    public Set<Object> distinctRandomMembers(String key, long count) {
        return redisTemplate.opsForSet().distinctRandomMembers(prefix + key, count);
    }

    /**
     * 从指定的可以中随机获取count个不同的元素（可能重复）
     *
     * @param key
     *         key
     * @param count
     *         个数
     *
     * @return
     */
    public List<Object> randomMembers(String key, long count) {
        return redisTemplate.opsForSet().randomMembers(prefix + key, count);
    }

    /**
     * 获取zSet操作对象
     *
     * @return zSet操作对象
     */

    public ZSetOperations<String, Object> getZSetOperations() {
        return redisTemplate.opsForZSet();
    }

    /**
     * 获取zeset的绑定对象
     *
     * @param key
     *         key
     *
     * @return
     */
    public BoundZSetOperations<String, Object> getBoundZSetOps(String key) {

        return redisTemplate.boundZSetOps(prefix + key);
    }

}
