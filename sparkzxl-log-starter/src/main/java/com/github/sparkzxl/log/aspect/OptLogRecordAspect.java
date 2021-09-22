package com.github.sparkzxl.log.aspect;

import cn.hutool.core.text.StrFormatter;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.github.sparkzxl.core.utils.AspectUtil;
import com.github.sparkzxl.core.utils.ListUtils;
import com.github.sparkzxl.core.utils.NetworkUtil;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;
import com.github.sparkzxl.log.annotation.OptLogRecord;
import com.github.sparkzxl.log.entity.OptLogRecordDetail;
import com.github.sparkzxl.log.store.ILogRecordService;
import com.github.sparkzxl.log.store.IOperatorService;
import com.google.common.collect.Lists;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * description:
 *
 * @author zhouxinlei
 * @date 2021-09-22 09:26:21
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class OptLogRecordAspect {

    private final ILogRecordService logRecordService;
    private final IOperatorService operatorService;

    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1,
            4,
            10,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(30),
            new DefaultThreadFactory("opt-log-record"));

    private final ThreadLocal<OptLogRecordDetail> logRecordDetailThreadLocal = new TransmittableThreadLocal<>();

    @Pointcut("@annotation(optLogRecord)")
    public void pointCut(OptLogRecord optLogRecord) {

    }

    @Before(value = "pointCut(optLogRecord)", argNames = "joinPoint,optLogRecord")
    public void beforeMethod(JoinPoint joinPoint, OptLogRecord optLogRecord) throws Throwable {
        String userId = operatorService.getUserId();
        String name = operatorService.getUserName();
        HttpServletRequest httpServletRequest = RequestContextHolderUtils.getRequest();
        log.info("用户行为记录：操作人【{}】,业务类型：【{}】,请求IP：【{}】,请求接口：【{}】", name,
                optLogRecord.category(),
                NetworkUtil.getIpAddress(httpServletRequest),
                httpServletRequest.getRequestURL().toString());

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
        OptLogRecordDetail logRecord = new OptLogRecordDetail()
                .setBizNo(bizNo)
                .setDetail(formatLogContent)
                .setCategory(optLogRecord.category())
                .setUserId(userId)
                .setOperator(name);
        set(logRecord);
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
        OptLogRecordDetail optLogRecordDetail = get();
        Object proceed = joinPoint.proceed();
        CompletableFuture.runAsync(() -> logRecordService.record(optLogRecordDetail), threadPoolExecutor);
        return proceed;
    }

    /**
     * 后置通知
     */
    @AfterReturning(value = "pointCut(optLogRecord)", argNames = "optLogRecord")
    public void afterReturning(OptLogRecord optLogRecord) {
        remove();
    }

    public OptLogRecordDetail get() {
        return logRecordDetailThreadLocal.get();
    }

    public void set(OptLogRecordDetail optLogRecordDetail) {
        logRecordDetailThreadLocal.set(optLogRecordDetail);
    }

    public void remove() {
        logRecordDetailThreadLocal.remove();
    }
}
