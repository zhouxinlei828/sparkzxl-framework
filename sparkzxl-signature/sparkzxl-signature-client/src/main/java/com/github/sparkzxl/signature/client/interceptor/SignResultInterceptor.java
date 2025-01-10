package com.github.sparkzxl.signature.client.interceptor;

import com.github.sparkzxl.signature.entity.SignResult;

/**
 * description: 加签结果拦截器
 *
 * @author zhouxinlei
 * @since 2024-05-21 17:47:00
 */
public interface SignResultInterceptor {

    /**
     * 处理签名结果
     *
     * @param signResult 签名返回体
     */
    void intercept(SignResult signResult);

}
