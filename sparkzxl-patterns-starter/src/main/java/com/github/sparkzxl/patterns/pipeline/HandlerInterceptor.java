package com.github.sparkzxl.patterns.pipeline;

/**
 * description: 处理器接口
 *
 * @author zhoux
 * @since 2021-07-20 22:06:24
 */
public interface HandlerInterceptor<R, T> {

    /**
     * 预先处理逻辑执行
     *
     * @param t 请求参数
     * @return 返回结果
     */
    R execute(T t);

}
