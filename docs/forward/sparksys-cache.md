# sparksys-cache-starter
> 职能：
> 对缓存通用的接口实现，包含本地缓存caffeine，redis，GuavaCache的实现，对原本赤裸裸的API做了通用性适配

## POM

```xml
<dependencies>
    <dependency>
        <groupId>com.sparksys</groupId>
        <artifactId>sparksys-core</artifactId>
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
- 新建CacheTemplate模板类
```java
import java.util.function.Function;
/**
 * description: 缓存提供接口
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:25:06
 */
public interface CacheTemplate {

    /**
     * 查询缓存
     *
     * @param key 缓存键 不可为空
     * @return T
     * @author zhouxinlei
     * @date 2020-01-27 20:19:27
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
     * @return void
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
     * @author zhouxinlei
     * @date 2019-10-11 16:23:58
     */
    Long decrement(String key);

    /**
     * 自减
     *
     * @param key   key值
     * @param delta 自减间距
     * @return Long
     * @author zhouxinlei
     * @date 2019-10-11 16:23:58
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
     *
     * @return void
     **/
    void flushDb();
}
```
- 实现CacheTemplate
1. GuavaTemplateImpl
2. RedisCacheTemplateImpl
3. CacheCaffeineTemplateImpl

- redis序列化，使用fastJson
```java
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * description: redis fastJson 序列化
 *
 * @author: zhouxinlei
 * @date: 2020-08-05 16:17:14
 */
public class FastJson2JsonRedisSerializer<T> implements RedisSerializer<T> {

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final Class<T> clazz;

    static {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
    }

    public FastJson2JsonRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }
        return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        String str = new String(bytes, DEFAULT_CHARSET);
        return JSON.parseObject(str, clazz);
    }
}
```
## 拓展
> 实际业务场景比较复杂，如果多种缓存并用，必然会出现不同步问题，如何才能设计出通用的缓存接口，具体可以参考[设计模式设计模式之抽象工厂模式：「替换多种缓存，代理抽象场景」](https://www.sparksys.top/archives/42)
