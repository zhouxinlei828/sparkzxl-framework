package com.github.sparkzxl.feign.exception;

import feign.Response;

/**
 * description: 异常定义定位器
 *
 * @author zhouxinlei
 * @since 2022-06-01 09:15:07
 */
public interface ExceptionDefinitionLocator {


    /**
     * 加载异常断言
     *
     * @return ExceptionPredicate<Response>
     */
    ExceptionPredicate<Response> getExceptionPredicate();

}