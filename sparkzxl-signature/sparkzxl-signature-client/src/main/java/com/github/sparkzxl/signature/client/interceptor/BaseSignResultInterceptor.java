package com.github.sparkzxl.signature.client.interceptor;

import lombok.extern.slf4j.Slf4j;
import com.github.sparkzxl.core.json.JsonUtils;
import com.github.sparkzxl.signature.entity.SignResult;

/**
 * description: 加签结果拦截器
 *
 * @author zhouxinlei
 * @since 2024-05-21 17:47:00
 */
@Slf4j
public abstract class BaseSignResultInterceptor implements SignResultInterceptor {

    @Override
    public void intercept(SignResult signResult) {
        log.info("Sign result is {}", JsonUtils.getJson().toJson(signResult));
        consumer(signResult);
    }

    public abstract void consumer(SignResult signResult);
}
