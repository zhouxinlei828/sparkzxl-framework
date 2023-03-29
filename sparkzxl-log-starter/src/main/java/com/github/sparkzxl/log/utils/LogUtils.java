package com.github.sparkzxl.log.utils;

import cn.hutool.core.annotation.AnnotationUtil;
import com.github.sparkzxl.log.annotation.HttpRequestLog;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * description: 日志工具类
 *
 * @author zhouxinlei
 * @since 2022-03-20 11:39:03
 */
@Slf4j
public class LogUtils {

    /**
     * 优先从子类获取 @SysLog： 1，若子类重写了该方法，有标记就记录日志，没标记就忽略日志 2，若子类没有重写该方法，就从父类获取，父类有标记就记录日志，没标记就忽略日志
     */
    public static HttpRequestLog getTargetAnnotation(JoinPoint point) {
        try {
            HttpRequestLog annotation = null;
            if (point.getSignature() instanceof MethodSignature) {
                Method method = ((MethodSignature) point.getSignature()).getMethod();
                if (AnnotationUtil.hasAnnotation(method, HttpRequestLog.class)) {
                    annotation = AnnotationUtil.getAnnotation(method, HttpRequestLog.class);
                } else {
                    annotation = AnnotationUtil.getAnnotation(point.getSignature().getDeclaringType(), HttpRequestLog.class);
                }
            }
            return annotation;
        } catch (Exception e) {
            log.warn("获取 {}.{} 的 @HttpRequestLog 注解失败", point.getSignature().getDeclaringTypeName(), point.getSignature().getName(),
                    e);
            return null;
        }
    }
}
