package com.github.sparkzxl.cache.utils;

import cn.hutool.core.util.IdUtil;
import com.github.sparkzxl.cache.template.CacheTemplate;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * description: redis token生成组件
 *
 * @author zhouxinlei
 */
public class TokenUtil {

    private final CacheTemplate cacheTemplate;

    public TokenUtil(CacheTemplate cacheTemplate) {
        this.cacheTemplate = cacheTemplate;
    }

    public String getToken() {
        String token = "token".concat(IdUtil.simpleUUID());
        cacheTemplate.set(token, token, 15L, TimeUnit.MINUTES);
        return token;
    }


    public boolean findToken(String token) {
        String value = cacheTemplate.get(token);
        if (!StringUtils.isEmpty(value)) {
            cacheTemplate.remove(token);
            return true;
        }
        return false;
    }
}
