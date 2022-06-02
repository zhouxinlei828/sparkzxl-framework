package com.github.sparkzxl.feign.util;

import com.github.sparkzxl.feign.annoation.RetryableMethod;
import feign.Request;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * description: feign工具类
 *
 * @author zhouxinlei
 * @since 2022-04-04 11:52:15
 */
public class OpenfeignUtil {
    /**
     * 判断一个 OpenFeign 的请求是否是可以重试类型的请求
     * 根据方法是否为 GET，以及方法和方法所在类上面是否有 RetryableMethod 注解来判定
     *
     * @param request 请求
     * @return boolean
     */
    public static boolean isRetryableRequest(Request request) {
        Request.HttpMethod httpMethod = request.httpMethod();
        if (Objects.equals(httpMethod, Request.HttpMethod.GET)) {
            return true;
        }
        Method method = request.requestTemplate().methodMetadata().method();
        RetryableMethod annotation = method.getAnnotation(RetryableMethod.class);
        if (annotation == null) {
            annotation = method.getDeclaringClass().getAnnotation(RetryableMethod.class);
        }
        //如果类上面或者方法上面有注解，则为查询类型的请求，是可以重试的
        return annotation != null;
    }
}
