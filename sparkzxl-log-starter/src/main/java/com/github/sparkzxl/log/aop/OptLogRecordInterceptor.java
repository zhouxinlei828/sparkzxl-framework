package com.github.sparkzxl.log.aop;

import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import com.github.sparkzxl.core.spring.SpringContextUtils;
import com.github.sparkzxl.core.util.AopUtil;
import com.github.sparkzxl.core.util.ArgumentAssert;
import com.github.sparkzxl.core.util.NetworkUtil;
import com.github.sparkzxl.core.util.RequestContextHolderUtils;
import com.github.sparkzxl.log.annotation.OptLogRecord;
import com.github.sparkzxl.log.handler.IOptLogVariablesHandler;
import com.github.sparkzxl.log.entity.OptLogRecordDetail;
import com.github.sparkzxl.log.event.OptLogEvent;
import com.github.sparkzxl.log.store.OperatorService;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * description: 用户操作行为aop处理器
 *
 * @author zhouxinlei
 * @since 2022-05-27 14:21:13
 */
public class OptLogRecordInterceptor implements MethodInterceptor {

    private final OperatorService operatorService;

    public OptLogRecordInterceptor(OperatorService operatorService) {
        this.operatorService = operatorService;
    }

    @Override
    public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        return execute(invocation, method, invocation.getArguments());
    }

    private Object execute(MethodInvocation invocation, Method method, Object[] args) throws Throwable {
        //fix 使用其他aop组件时,aop切了两次.
        Class<?> cls = AopProxyUtils.ultimateTargetClass(Objects.requireNonNull(invocation.getThis()));
        if (!cls.equals(invocation.getThis().getClass())) {
            return invocation.proceed();
        }
        Object proceed = invocation.proceed();
        OptLogRecord annotation = invocation.getMethod().getAnnotation(OptLogRecord.class);
        String userId = operatorService.getUserId();
        String name = operatorService.getUserName();
        HttpServletRequest httpServletRequest = RequestContextHolderUtils.getRequest();
        String bizNo = "";
        if (StringUtils.isNotBlank(annotation.bizNo())) {
            bizNo = AopUtil.parseExpression(invocation, annotation.bizNo());
        }
        OptLogRecordDetail optLogRecordDetail = new OptLogRecordDetail()
                .setIp(NetworkUtil.getIpAddress(httpServletRequest))
                .setRequestUrl(httpServletRequest.getRequestURL().toString())
                .setBizNo(bizNo)
                .setCategory(annotation.category())
                .setUserId(userId)
                .setOperator(name)
                .setTenantId(RequestLocalContextHolder.getTenant());
        if (StringUtils.isNotBlank(annotation.template())) {
            Map<String, Object> alarmParamMap = getVariablesHandler(annotation.variablesBeanName()).getVariables(method, args, annotation);
            ExpressionParser parser = new SpelExpressionParser();
            TemplateParserContext parserContext = new TemplateParserContext();
            String message = parser.parseExpression(annotation.template(), parserContext).getValue(alarmParamMap, String.class);
            optLogRecordDetail.setDetail(message);
        }
        SpringContextUtils.publishEvent(new OptLogEvent(optLogRecordDetail));
        return proceed;
    }

    private IOptLogVariablesHandler getVariablesHandler(String variablesBeanName) {
        IOptLogVariablesHandler optLogVariablesHandler = SpringContextUtils.getBean(variablesBeanName);
        ArgumentAssert.notNull(optLogVariablesHandler, "操作日志未指定变量处理器，请联系管理员");
        return optLogVariablesHandler;
    }
}
