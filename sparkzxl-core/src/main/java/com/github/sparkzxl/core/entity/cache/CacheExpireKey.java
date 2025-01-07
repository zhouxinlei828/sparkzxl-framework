package com.github.sparkzxl.core.entity.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

/**
 * description: 缓存 key 实体类封装
 *
 * @author zhouxinlei
 * @since 2024-04-18 09:13:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CacheExpireKey {
    /**
     * redis key
     */
    @NonNull
    private String key;
    /**
     * redis key
     */
    @NonNull
    private String type;
    /**
     * 过期时间
     */
    private Long expire;

    public CacheExpireKey(final @NonNull String key) {
        this.key = key;
    }
}
