package com.github.sparkzxl.lock;

import cn.hutool.core.text.StrPool;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

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
    public String buildKey(MethodInvocation invocation, String[] definitionKeys) {
        Method method = invocation.getMethod();
        if (definitionKeys.length > 1 || !"".equals(definitionKeys[0])) {
            return getSpElDefinitionKey(definitionKeys, method, invocation.getArguments());
        }
        return "";
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
