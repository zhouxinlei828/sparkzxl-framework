package com.github.sparkzxl.log;

import wiki.xsx.core.support.MethodInfo;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * description: 默认用户行为回调
 *
 * @author zhouxinlei
 */
public class DefaultUserBehaviorLogCallback implements UserBehaviorLogCallback {
    @Override
    public String callback(Annotation annotation, MethodInfo methodInfo, Map<String, Object> paramMap) {
        return UserBehaviorLogCallback.super.callback(annotation, methodInfo, paramMap);
    }
}
