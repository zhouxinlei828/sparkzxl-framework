package com.github.sparkzxl.signature.client.aspect;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ReflectUtil;
import com.github.sparkzxl.signature.client.annotation.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import com.github.sparkzxl.core.spring.SpringContextUtils;
import com.github.sparkzxl.core.util.AopUtil;
import com.github.sparkzxl.core.util.DateUtils;
import com.github.sparkzxl.signature.client.annotation.*;
import com.github.sparkzxl.signature.client.interceptor.SignResultInterceptor;
import com.github.sparkzxl.signature.entity.SignResult;
import com.github.sparkzxl.signature.executor.SignatureExecutor;
import com.github.sparkzxl.signature.executor.SignatureExecutorContext;
import com.github.sparkzxl.signature.properties.SignatureProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;

/**
 * description: 加签aop处理器
 *
 * @author zhouxinlei
 * @since 2022-05-27 14:21:13
 */
@Aspect
public class SignMethodAspect {

    public static final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Autowired
    private SignatureExecutorContext signatureExecutorContext;
    @Autowired
    private SignatureProperties signatureProperties;

    private final Function<String, String> function;

    public SignMethodAspect(Function<String, String> function) {
        this.function = function;
    }


    @Pointcut("@within(com.github.sparkzxl.signature.client.annotation.Sign)|| @annotation(com.github.sparkzxl.signature.client.annotation.Sign)")
    public void pointCut() {

    }

    @Around("pointCut()")
    public Object invoke(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = AopUtil.getTargetMethod(joinPoint);
        Sign signAnnotation = method.getAnnotation(Sign.class);
        String appKey;
        if (StringUtils.isEmpty(signAnnotation.value())) {
            // 从上下文获取
            appKey = function.apply("appKey");
        } else {
            appKey = signAnnotation.value();
        }
        Object signData = AopUtil.getParameterAnnotationData(method, joinPoint.getArgs(), SignField.class, null);
        Map<String, SignatureProperties.AppProperties> provider = signatureProperties.getProvider();
        SignatureProperties.AppProperties properties = provider.get(appKey);
        SignatureExecutor signatureExecutor = signatureExecutorContext.getExecutor(properties.getSignType().name());
        // 生成签名
        SignResult signResult = signatureExecutor.sign(appKey, signData);
        Class<? extends SignResultInterceptor> handler = signAnnotation.handler();
        SignResultInterceptor signResultInterceptor = SpringContextUtils.getBean(handler);
        signResultInterceptor.intercept(signResult);

        // 设置注解为signResult字段，实现自动注入加签结果
        Object[] args = joinPoint.getArgs();
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            final Object target = args[i];
            final Field[] fields = target.getClass().getDeclaredFields();
            for (Field field : fields) {
                boolean hasSignData = AnnotationUtil.hasAnnotation(field, SignData.class);
                if (hasSignData) {
                    String signVal = signResult.getSign();
                    ReflectUtil.setFieldValue(target, field, signVal);
                }
                boolean hasSignNonce = AnnotationUtil.hasAnnotation(field, SignNonce.class);
                if (hasSignNonce) {
                    String nonceVal = signResult.getNonce();
                    ReflectUtil.setFieldValue(target, field, nonceVal);
                }
                SignTime signTime = AnnotationUtil.getAnnotation(field, SignTime.class);
                if (ObjectUtils.isNotEmpty(signTime)) {
                    Class<?> type = signTime.type();
                    if (type.getSimpleName().equals(Long.class.getSimpleName())) {
                        Long timestamp = signResult.getTimestamp();
                        ReflectUtil.setFieldValue(target, field, timestamp);
                    } else if (type.getSimpleName().equals(String.class.getSimpleName())) {
                        Long timestamp = signResult.getTimestamp();
                        String format = signTime.format();
                        if (StringUtils.isEmpty(format)) {
                            ReflectUtil.setFieldValue(target, field, String.valueOf(timestamp));
                        } else {
                            DateTime dateTime = DateUtils.date(timestamp);
                            ReflectUtil.setFieldValue(target, field, DateUtils.format(dateTime, format));
                        }
                    } else if (type.getSimpleName().equals(LocalDateTime.class.getSimpleName())) {
                        Long timestamp = signResult.getTimestamp();
                        LocalDateTime localDateTime = LocalDateTimeUtil.of(timestamp);
                        ReflectUtil.setFieldValue(target, field, localDateTime);
                    }
                }
            }
        }
        return joinPoint.proceed(args);
    }
}
