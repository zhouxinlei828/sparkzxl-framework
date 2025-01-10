package com.github.sparkzxl.signature.server.cache;

import java.io.Serializable;

/**
 * description: 签名缓存
 *
 * @author zhouxinlei
 * @since 2024-05-20 16:35:38
 */
public interface SignCache {

    /**
     * Set cache
     *
     * @param key   Cache key
     * @param value Cache value after serialization
     */
    void set(String key, Serializable value);

    /**
     * Set the cache and specify the expiration time of the cache
     *
     * @param key     Cache key
     * @param value   Cache value after serialization
     * @param timeout The expiration time of the cache, in milliseconds
     */
    void set(String key, Serializable value, long timeout);

    /**
     * Get cache value
     *
     * @param key Cache key
     * @return Cache value
     */
    Serializable get(String key);

    /**
     * Determine whether a key exists in the cache
     *
     * @param key Cache key
     * @return boolean
     */
    boolean containsKey(String key);

    /**
     * Delete the key from the cache
     *
     * @param key Cache key
     */
    void removeKey(String key);

}
