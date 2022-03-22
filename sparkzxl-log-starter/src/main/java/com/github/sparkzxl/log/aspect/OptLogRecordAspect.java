package com.github.sparkzxl.log.aspect;

import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import com.github.sparkzxl.core.spring.SpringContextUtils;
import com.github.sparkzxl.core.util.AspectUtil;
import com.github.sparkzxl.core.util.NetworkUtil;
import com.github.sparkzxl.core.util.RequestContextHolderUtils;
import com.github.sparkzxl.log.annotation.OptLogRecord;
import com.github.sparkzxl.log.entity.OptLogRecordDetail;
import com.github.sparkzxl.log.event.OptLogEvent;
import com.github.sparkzxl.log.store.OperatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * description: 操作行为日志切面
 *
 * @author zhouxinlei
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class OptLogRecordAspect {

    private final OperatorService operatorService;
    private final ILogAttribute logAttribute;

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
    @AfterReturning(returning = "ret", pointcut = "pointCut(optLogRecord)", argNames = "joinPoint,optLogRecord,ret")
    public Object around(JoinPoint joinPoint, OptLogRecord optLogRecord, Object ret) throws Throwable {
        String userId = operatorService.getUserId();
        String name = operatorService.getUserName();
        HttpServletRequest httpServletRequest = RequestContextHolderUtils.getRequest();
        String bizNo = "";
        if (StringUtils.isNotBlank(optLogRecord.bizNo())) {
            bizNo = AspectUtil.parseExpression(joinPoint, optLogRecord.bizNo());
        }
        OptLogRecordDetail optLogRecordDetail = new OptLogRecordDetail()
                .setIp(NetworkUtil.getIpAddress(httpServletRequest))
                .setRequestUrl(httpServletRequest.getRequestURL().toString())
                .setBizNo(bizNo)
                .setCategory(optLogRecord.category())
                .setUserId(userId)
                .setOperator(name)
                .setTenantId(RequestLocalContextHolder.getTenant());
        buildLogTemplate(joinPoint, optLogRecord, optLogRecordDetail);
        SpringContextUtils.publishEvent(new OptLogEvent(optLogRecordDetail));
        return ret;
    }

    private void buildLogTemplate(JoinPoint joinPoint, OptLogRecord optLogRecord, OptLogRecordDetail optLogRecordDetail) {
        if (StringUtils.isNotBlank(optLogRecord.template())) {
            Map<String, Object> alarmParamMap = logAttribute.getAttributes(joinPoint, optLogRecord);
            ExpressionParser parser = new SpelExpressionParser();
            TemplateParserContext parserContext = new TemplateParserContext();
            String message = parser.parseExpression(optLogRecord.template(), parserContext).getValue(alarmParamMap, String.class);
            optLogRecordDetail.setDetail(message);
        }
    }

}
