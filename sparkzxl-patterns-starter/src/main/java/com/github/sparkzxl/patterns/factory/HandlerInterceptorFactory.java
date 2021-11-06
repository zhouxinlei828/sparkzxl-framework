package com.github.sparkzxl.patterns.factory;

import com.github.sparkzxl.patterns.pipeline.HandlerInterceptor;

import java.util.List;

/**
 * description: 责任链工厂
 *
 * @author zhoux
 * @date 2021-10-23 12:57:46
 */
public interface HandlerInterceptorFactory {

    /**
     * 获取责任链执行器
     *
     * @param type 业务类型
     * @return List<HandlerInterceptor>
     */
    List<HandlerInterceptor> getInterceptorList(String type);
}
