package com.github.sparkzxl.patterns.duty;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 链路管理器
 *
 * @author zhoux
 * @date 2021-07-20 22:06:01
 */
public class HandlerChain<R, T> {

    private final List<HandlerInterceptor<R, T>> handlerInterceptorList = new ArrayList<>();

    /**
     * 添加处理器
     *
     * @param handlerInterceptor 处理器
     */
    public HandlerChain<R, T> addHandler(HandlerInterceptor<R, T> handlerInterceptor) {
        handlerInterceptorList.add(handlerInterceptor);
        return this;
    }

    /**
     * 执行处理
     *
     * @param t 请求参数
     * @return R
     */
    public R execute(T t) {
        R r = null;
        if (!handlerInterceptorList.isEmpty()) {
            for (HandlerInterceptor<R, T> interceptor : handlerInterceptorList) {
                r = interceptor.handle(t);
            }
        }
        return r;
    }
}
