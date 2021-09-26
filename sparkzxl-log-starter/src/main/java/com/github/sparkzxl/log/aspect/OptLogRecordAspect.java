package com.github.sparkzxl.log.aspect;

import cn.hutool.core.text.StrFormatter;
import com.github.sparkzxl.core.context.AppContextHolder;
import com.github.sparkzxl.core.spring.SpringContextUtils;
import com.github.sparkzxl.core.utils.AspectUtil;
import com.github.sparkzxl.core.utils.ListUtils;
import com.github.sparkzxl.core.utils.NetworkUtil;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;
import com.github.sparkzxl.log.annotation.OptLogRecord;
import com.github.sparkzxl.log.entity.OptLogRecordDetail;
import com.github.sparkzxl.log.event.OptLogEvent;
import com.github.sparkzxl.log.store.IOperatorService;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * description: 操作行为日志切面
 *
 * @author zhouxinlei
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class OptLogRecordAspect {

    private final IOperatorService operatorService;

    @Pointcut("@annotation(optLogRecord)")
    public void pointCut(OptLogRecord optLogRecord) {

    }

    /**
     * 环绕操作
     *
     * @param joinPoint    切入点
     * @param optLogRecord 日志注解
     * @return 原方法返回值
     * @throws Throwable 异常信息
     */
    @Around(value = "pointCut(optLogRecord)", argNames = "joinPoint,optLogRecord")
    public Object around(ProceedingJoinPoint joinPoint, OptLogRecord optLogRecord) throws Throwable {
        String userId = operatorService.getUserId();
        String name = operatorService.getUserName();
        HttpServletRequest httpServletRequest = RequestContextHolderUtils.getRequest();
        String conditionExpression = optLogRecord.condition();
        String bizNo = "";
        if (StringUtils.isNotBlank(optLogRecord.bizNo())) {
            bizNo = AspectUtil.parseExpression(joinPoint, optLogRecord.bizNo());
        }
        List<String> variables = Lists.newArrayList();
        if (StringUtils.isNotBlank(conditionExpression)) {
            List<String> conditionList = ListUtils.stringToList(conditionExpression);
            conditionList.forEach(condition -> {
                try {
                    String value = AspectUtil.parseExpression(joinPoint, condition);
                    variables.add(value);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            });
        }
        Object[] params = ListUtils.listToArray(variables);
        String formatLogContent = StrFormatter.format(optLogRecord.detail(), params);
        OptLogRecordDetail optLogRecordDetail = new OptLogRecordDetail()
                .setIp(NetworkUtil.getIpAddress(httpServletRequest))
                .setRequestUrl(httpServletRequest.getRequestURL().toString())
                .setBizNo(bizNo)
                .setDetail(formatLogContent)
                .setCategory(optLogRecord.category())
                .setUserId(userId)
                .setOperator(name)
                .setTenantId(AppContextHolder.getTenant());
        Object proceed = joinPoint.proceed();
        SpringContextUtils.publishEvent(new OptLogEvent(optLogRecordDetail));
        return proceed;
    }
}
