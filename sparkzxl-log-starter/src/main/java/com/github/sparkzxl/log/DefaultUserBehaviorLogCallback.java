package com.github.sparkzxl.log;

import wiki.xsx.core.support.MethodInfo;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * description: 默认用户行为回调
 *
 * @author zhouxinlei
 * @date 2021-05-23 21:07:56
 */
public class DefaultUserBehaviorLogCallback implements UserBehaviorLogCallback {
    @Override
    public String callback(Annotation annotation, MethodInfo methodInfo, Map<String, Object> paramMap) {
        return UserBehaviorLogCallback.super.callback(annotation, methodInfo, paramMap);
    }
}
