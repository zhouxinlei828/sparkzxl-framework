# sparkzxl-cache-starter

> 职能：
> 对缓存通用的接口实现，包含本地缓存caffeine，redis，GuavaCache的实现，对原本赤裸裸的API做了通用性适配

## POM

```xml

<dependencies>
    <dependency>
        <groupId>com.github.sparkzxl</groupId>
        <artifactId>sparkzxl-core</artifactId>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-pool2</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
    <dependency>
        <groupId>redis.clients</groupId>
        <artifactId>jedis</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-configuration-processor</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>com.google.guava</groupId>
        <artifactId>guava</artifactId>
    </dependency>
    <dependency>
        <groupId>com.github.ben-manes.caffeine</groupId>
        <artifactId>caffeine</artifactId>
    </dependency>
</dependencies>
```

## 实现

- 新建GeneralCacheService类

```java
import java.util.function.Function;

/**
 * description: 缓存提供接口
 *
 * @author zhouxinlei
 */
public interface GeneralCacheService {

    /**
     * 查询缓存
     *
     * @param key 缓存键 不可为空
     * @return T
     */
    <T> T get(String key);

    /**
     * 查询缓存
     *
     * @param key      缓存键 不可为空
     * @param function 如没有缓存，调用该callable函数返回对象 可为空
     * @return T
     */
    <T> T get(String key, Function<String, T> function);

    /**
     * 查询缓存
     *
     * @param key       缓存键 不可为空
     * @param function  如没有缓存，调用该callable函数返回对象 可为空
     * @param funcParam function函数的调用参数
     * @return T
     */
    <T, M> T get(String key, Function<M, T> function, M funcParam);

    /**
     * 查询缓存
     *
     * @param key        缓存键 不可为空
     * @param function   如没有缓存，调用该callable函数返回对象 可为空
     * @param expireTime 过期时间（单位：毫秒） 可为空
     * @return T
     **/
    <T> T get(String key, Function<String, T> function, Long expireTime);

    /**
     * 查询缓存
     *
     * @param key        缓存键 不可为空
     * @param function   如没有缓存，调用该callable函数返回对象 可为空
     * @param funcParam  function函数的调用参数
     * @param expireTime 过期时间（单位：毫秒） 可为空
     * @return T
     **/
    <T, M> T get(String key, Function<M, T> function, M funcParam, Long expireTime);

    /**
     * 设置缓存键值
     *
     * @param key 缓存键 不可为空
     * @param obj 缓存值 不可为空
     **/
    void set(String key, Object obj);

    /**
     * 设置缓存键值
     *
     * @param key        缓存键 不可为空
     * @param value      缓存值 不可为空
     * @param expireTime 过期时间（单位：毫秒） 可为空
     * @return void
     **/
    void set(String key, Object value, Long expireTime);

    /**
     * 自增长
     *
     * @param key key值
     * @return Long
     */
    Long increment(String key);

    /**
     * 自增长
     *
     * @param key   key值
     * @param delta 自增间距
     * @return Long
     */
    Long increment(String key, long delta);

    /**
     * 自减
     *
     * @param key key值
     * @return Long
     */
    Long decrement(String key);

    /**
     * 自减
     *
     * @param key   key值
     * @param delta 自减间距
     * @return Long
     */
    Long decrement(String key, long delta);

    /**
     * 移除缓存
     *
     * @param keys 缓存键 不可为空
     * @return Long
     */
    Long remove(String... keys);

    /**
     * 是否存在缓存
     *
     * @param key 缓存键 不可为空
     * @return boolean
     **/
    boolean exists(String key);

    /**
     * 刷新DB
     **/
    void flushDb();
}
```

- 实现GeneralCacheService类

1. RedisCacheImpl
2. CaffeineCacheImpl

- redis序列化，使用jackson序列化

```java
package com.github.sparkzxl.cache.serializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.github.sparkzxl.core.jackson.JsonUtil;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

/**
 * description: jackson序列化
 *
 * @author zhouxinlei
 */

public class RedisObjectSerializer extends Jackson2JsonRedisSerializer<Object> {

    public RedisObjectSerializer() {
        super(Object.class);
        ObjectMapper objectMapper = JsonUtil.newInstance();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.WRAPPER_ARRAY);
        this.setObjectMapper(objectMapper);
    }
}

```

## 拓展

> 实际业务场景比较复杂，如果多种缓存并用，必然会出现不同步问题，如何才能设计出通用的缓存接口，具体可以参考[设计模式设计模式之抽象工厂模式：「替换多种缓存，代理抽象场景」](https://www.sparkzxl.top/archives/42)

## 使用方法

1. 引入依赖

```xml

<dependency>
    <groupId>com.github.sparkzxl</groupId>
    <artifactId>sparkzxl-cache-starter</artifactId>
    <version>${sparkzxl.version}</version>
</dependency>
```

# 公众号

学习不走弯路，关注公众号「凛冬王昭君」

![wechat-sparkzxl.jpg](https://oss.sparksys.top/sparkzxl-component/wechat-sparkzxl.jpg)
