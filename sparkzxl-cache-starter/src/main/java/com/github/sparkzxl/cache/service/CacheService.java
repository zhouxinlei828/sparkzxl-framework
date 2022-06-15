package com.github.sparkzxl.cache.service;

import com.github.sparkzxl.cache.redis.CacheHashKey;
import com.github.sparkzxl.cache.redis.CacheKey;
import org.springframework.lang.NonNull;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * description: 缓存提供接口
 *
 * @author zhouxinlei
 */
public interface CacheService {

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
     * @param key      缓存键 不可为空
     * @param function 如没有缓存，调用该callable函数返回对象 可为空
     * @param timeout  过期时间 可为空
     * @return T
     * @see TimeUnit
     **/
    <T> T get(String key, Function<String, T> function, Duration timeout);

    /**
     * 查询缓存
     *
     * @param key       缓存键 不可为空
     * @param function  如没有缓存，调用该callable函数返回对象 可为空
     * @param funcParam function函数的调用参数
     * @param timeout   过期时间 可为空
     * @return T
     * @see TimeUnit
     **/
    <T, M> T get(String key, Function<M, T> function, M funcParam, Duration timeout);

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
     * @param key     缓存键 不可为空
     * @param value   缓存值 不可为空
     * @param timeout 过期时间 可为空
     * @see TimeUnit
     **/
    void set(String key, Object value, Duration timeout);

    /**
     * 不存在时设置缓存键值对
     *
     * @param key     键值
     * @param value   value值
     * @param timeout 过期时间
     * @return boolean
     */
    boolean setIfAbsent(String key, Object value, Duration timeout);

    /**
     * 不存在时设置缓存键值对
     *
     * @param key   键值
     * @param value value值
     * @return boolean
     */
    boolean setIfAbsent(String key, Object value);

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
     */
    void remove(String... keys);

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

    /**
     * 查找所有符合给定模式 pattern 的 key 。
     * <p>
     * 例子：
     * KEYS * 匹配数据库中所有 key 。
     * KEYS h?llo 匹配 hello ， hallo 和 hxllo 等。
     * KEYS a*cde 匹配 acde 和 aeeeeecde 等。
     * KEYS h[ae]llo 匹配 hello 和 hallo ，但不匹配 hillo 。
     * <p>
     * 特殊符号用 \ 隔开
     *
     * @param pattern 表达式
     * @return 符合给定模式的 key 列表
     */
    Set<String> keys(@NonNull String pattern);

    /**
     * 查找所有符合给定模式 pattern 的 key 。
     * <p>
     * 例子：
     * KEYS * 匹配数据库中所有 key 。
     * KEYS h?llo 匹配 hello ， hallo 和 hxllo 等。
     * KEYS a*cde 匹配 acde 和 aeeeeecde 等。
     * KEYS h[ae]llo 匹配 hello 和 hallo ，但不匹配 hillo 。
     * <p>
     * 特殊符号用 \ 隔开
     *
     * @param pattern 表达式
     * @return 符合给定模式的 key 列表
     */
    List<String> scan(@NonNull String pattern);

    /**
     * 查找所有符合给定模式 pattern 的 key ,并将其删除
     * <p>
     * 例子：
     * KEYS * 匹配数据库中所有 key 。
     * KEYS h?llo 匹配 hello ， hallo 和 hxllo 等。
     * KEYS a*cde 匹配 acde 和 aeeeeecde 等。
     * KEYS h[ae]llo 匹配 hello 和 hallo ，但不匹配 hillo 。
     * <p>
     * 特殊符号用 \ 隔开
     *
     * @param pattern 表达式
     */
    void scanUnlink(@NonNull String pattern);

    /**
     * 为给定 key 设置生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 是否成功
     */
    Boolean expire(@NonNull CacheKey key);

    /**
     * 移除给定 key 的生存时间，将这个 key 从『易失的』(带生存时间 key )转换成『持久的』(一个不带生存时间、永不过期的 key )。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 是否成功
     */
    Boolean persist(@NonNull CacheKey key);

    /**
     * 返回 key 所储存的值的类型。
     *
     * @param key 一定不能为 {@literal null}.
     * @return none (key不存在)、string (字符串)、list (列表)、set (集合)、zset (有序集)、hash (哈希表) 、stream （流）、caffeine（内存）
     */
    String type(@NonNull CacheKey key);

    /**
     * 以秒为单位，返回给定 key 的剩余生存时间(TTL, time to live)。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 当 key 不存在时，返回 -2 。 当 key 存在但没有设置剩余生存时间时，返回 -1 。 否则，以秒为单位，返回 key 的剩余生存时间。
     */
    Long ttl(@NonNull CacheKey key);

    /**
     * 以毫秒为单位，返回给定 key 的剩余生存时间(TTL, time to live)。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 当 key 不存在时，返回 -2 。当 key 存在但没有设置剩余生存时间时，返回 -1 。否则，以毫秒为单位，返回 key 的剩余生存时间
     */
    Long pTtl(@NonNull CacheKey key);

    /**
     * 将哈希表 key 中的域 field 的值设为 value 。
     *
     * @param key             一定不能为 {@literal null}.
     * @param value           值
     * @param cacheNullValues 是否缓存空对象
     */
    void hSet(@NonNull CacheHashKey key, Object value, boolean... cacheNullValues);

    /**
     * 返回哈希表 key 中给定域 field 的值。
     *
     * @param key             一定不能为 {@literal null}.
     * @param cacheNullValues 是否缓存空值
     * @return 默认情况下返回给定域的值, 如果给定域不存在于哈希表中， 又或者给定的哈希表并不存在， 那么命令返回 nil
     */
    <T> T hGet(@NonNull CacheHashKey key, boolean... cacheNullValues);

    /**
     * 返回哈希表 key 中给定域 field 的值。
     *
     * @param key             一定不能为 {@literal null}.
     * @param loader          加载器
     * @param cacheNullValues 是否缓存空值
     * @return 默认情况下返回给定域的值, 如果给定域不存在于哈希表中， 又或者给定的哈希表并不存在， 那么命令返回 nil
     */
    <T> T hGet(@NonNull CacheHashKey key, Function<CacheHashKey, T> loader, boolean... cacheNullValues);

    /**
     * 检查给定域 field 是否存在于哈希表 hash 当中
     *
     * @param cacheHashKey 一定不能为 {@literal null}.
     * @return 是否存在
     */
    Boolean hExists(@NonNull CacheHashKey cacheHashKey);

    /**
     * 删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
     *
     * @param key    一定不能为 {@literal null}.
     * @param fields 一定不能为 {@literal null}.
     * @return 删除的数量
     */
    Long hDel(@NonNull String key, Object... fields);

    /**
     * 删除哈希表 key 中的指定域，不存在的域将被忽略。
     *
     * @param cacheHashKey 一定不能为 {@literal null}.
     * @return 删除的数量
     */
    Long hDel(@NonNull CacheHashKey cacheHashKey);

    /**
     * 返回哈希表 key 中域的数量。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 哈希表中域的数量。
     */
    Long hLen(@NonNull CacheHashKey key);

    /**
     * 为哈希表 key 中的域 field 的值加上增量 increment 。
     *
     * @param key       一定不能为 {@literal null}.
     * @param increment 增量
     * @return 执行 HINCRBY 命令之后，哈希表 key 中域 field 的值
     */
    Long hIncrBy(@NonNull CacheHashKey key, long increment);

    /**
     * 为哈希表 key 中的域 field 的值加上增量 increment 。
     *
     * @param key       一定不能为 {@literal null}.
     * @param increment 增量
     * @return 执行 HINCRBY 命令之后，哈希表 key 中域 field 的值
     */
    Double hIncrBy(@NonNull CacheHashKey key, double increment);

    /**
     * 返回哈希表 key 中的所有域。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 所有的 filed
     */
    Set<Object> hKeys(@NonNull CacheHashKey key);

    /**
     * 返回哈希表 key 中所有域的值。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 一个包含哈希表中所有值的表。
     */
    List<Object> hVals(@NonNull CacheHashKey key);

    /**
     * 返回哈希表 key 中，所有的域和值。
     * 在返回值里，紧跟每个域名(field name)之后是域的值(value)，所以返回值的长度是哈希表大小的两倍。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 以列表形式返回哈希表的域和域的值
     */
    <K, V> Map<K, V> hGetAll(@NonNull CacheHashKey key);

    /**
     * 返回哈希表 key 中，所有的域和值。
     * 在返回值里，紧跟每个域名(field name)之后是域的值(value)，所以返回值的长度是哈希表大小的两倍。
     *
     * @param key             一定不能为 {@literal null}.
     * @param loader          加载回调
     * @param cacheNullValues 缓存空值
     * @return 以列表形式返回哈希表的域和域的值
     */
    <K, V> Map<K, V> hGetAll(@NonNull CacheHashKey key, Function<CacheHashKey, Map<K, V>> loader, boolean... cacheNullValues);

    /**
     * 将一个或多个 member 元素加入到集合 key 当中，已经存在于集合的 member 元素将被忽略。
     * 假如 key 不存在，则创建一个只包含 member 元素作成员的集合。
     * 当 key 不是集合类型时，返回一个错误。
     *
     * @param key   一定不能为 {@literal null}.
     * @param value 值
     * @return 被添加到集合中的新元素的数量，不包括被忽略的元素。
     */
    Long sAdd(@NonNull CacheKey key, Object value);

    /**
     * 移除集合 key 中的一个或多个 member 元素，不存在的 member 元素会被忽略。
     * 当 key 不是集合类型，返回一个错误。
     *
     * @param key     一定不能为 {@literal null}.
     * @param members 元素
     * @return 被成功移除的元素的数量，不包括被忽略的元素
     */
    Long sRem(@NonNull CacheKey key, Object... members);

    /**
     * 返回集合 key 中的所有成员。
     * 不存在的 key 被视为空集合。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 集合中的所有成员。
     */
    Set<Object> sMembers(@NonNull CacheKey key);

    /**
     * 移除并返回集合中的一个随机元素。
     * 如果只想获取一个随机元素，但不想该元素从集合中被移除的话，可以使用 SRANDMEMBER 命令。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 被移除的随机元素。 当 key 不存在或 key 是空集时，返回 nil 。
     */
    <T> T sPop(@NonNull CacheKey key);

    /**
     * 返回集合 key 的基数(集合中元素的数量)。
     *
     * @param key 一定不能为 {@literal null}.
     * @return 集合的基数。 当 key 不存在时，返回 0 。
     */
    Long sCard(@NonNull CacheKey key);


}
