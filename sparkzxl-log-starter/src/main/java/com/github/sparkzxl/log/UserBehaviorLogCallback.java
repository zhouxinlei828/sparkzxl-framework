package com.github.sparkzxl.log;

import wiki.xsx.core.support.MethodInfo;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * description: 用户行为日志回调
 *
 * @author zhouxinlei
 * @date 2021-05-23 20:53:33
 */
public interface UserBehaviorLogCallback {

    /**
     * 回调
     *
     * @param annotation 注解
     * @param methodInfo 方法信息
     * @param paramMap   参数
     * @return String
     */
    default String callback(Annotation annotation, MethodInfo methodInfo, Map<String, Object> paramMap) {
        return "";
    }

}
