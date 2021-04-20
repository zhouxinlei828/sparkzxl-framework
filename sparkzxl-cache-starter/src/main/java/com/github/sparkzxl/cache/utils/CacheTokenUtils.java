package com.github.sparkzxl.cache.utils;

import cn.hutool.core.util.IdUtil;
import com.github.sparkzxl.cache.template.GeneralCacheService;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * description: redis token生成组件
 *
 * @author zhouxinlei
 */
public class CacheTokenUtils {

    private final GeneralCacheService generalCacheService;

    public CacheTokenUtils(GeneralCacheService generalCacheService) {
        this.generalCacheService = generalCacheService;
    }

    public String getToken() {
        String token = "token".concat(IdUtil.simpleUUID());
        generalCacheService.set(token, token, 15L, TimeUnit.MINUTES);
        return token;
    }


    public boolean findToken(String token) {
        String value = generalCacheService.get(token);
        if (!StringUtils.isEmpty(value)) {
            generalCacheService.remove(token);
            return true;
        }
        return false;
    }
}
