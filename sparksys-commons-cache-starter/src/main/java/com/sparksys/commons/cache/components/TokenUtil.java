package com.sparksys.commons.cache.components;

import cn.hutool.core.util.IdUtil;
import com.sparksys.commons.core.repository.CacheRepository;
import org.springframework.util.StringUtils;

/**
 * description: redis token生成组件
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:29:13
 */
public class TokenUtil {

    private final CacheRepository cacheRepository;

    public TokenUtil(CacheRepository cacheRepository) {
        this.cacheRepository = cacheRepository;
    }

    public String getToken() {
        String token = "token".concat(IdUtil.simpleUUID());
        long expire = 60 * 60;
        cacheRepository.set(token, token, expire);
        return token;
    }


    public boolean findToken(String token) {
        String value = cacheRepository.get(token);
        if (!StringUtils.isEmpty(value)) {
            cacheRepository.remove(token);
            return true;
        }
        return false;
    }
}
