package com.github.sparkzxl.lock;

import cn.hutool.core.text.StrPool;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * description: 分布式锁Key生成器
 *
 * @author zhouxinlei
 * @since 2022-05-01 21:49:43
 */
public class DefaultLockKeyBuilder implements LockKeyBuilder {

    private static final ParameterNameDiscoverer NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    @Override
    public String buildKey(JoinPoint joinPoint, String[] definitionKeys) {
        if (definitionKeys.length > 1 || !"".equals(definitionKeys[0])) {
            Method targetMethod;
            try {
                targetMethod = getTargetMethod(joinPoint);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            return getSpElDefinitionKey(definitionKeys, targetMethod, joinPoint.getArgs());
        }
        return "";
    }

    public static Method getTargetMethod(JoinPoint pjp) throws NoSuchMethodException {
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method agentMethod = methodSignature.getMethod();
        return pjp.getTarget().getClass().getMethod(agentMethod.getName(), agentMethod.getParameterTypes());
    }

    protected String getSpElDefinitionKey(String[] definitionKeys, Method method, Object[] parameterValues) {
        EvaluationContext context = new MethodBasedEvaluationContext(new Object(), method, parameterValues,
                NAME_DISCOVERER);
        List<String> definitionKeyList = new ArrayList<>(definitionKeys.length);
        for (String definitionKey : definitionKeys) {
            if (definitionKey != null && !definitionKey.isEmpty()) {
                String key = PARSER.parseExpression(definitionKey).getValue(context, String.class);
                definitionKeyList.add(key);
            }
        }
        return StringUtils.collectionToDelimitedString(definitionKeyList, StrPool.COLON, "", "");
    }

}
