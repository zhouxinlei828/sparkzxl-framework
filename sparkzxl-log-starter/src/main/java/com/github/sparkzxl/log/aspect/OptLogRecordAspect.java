package com.github.sparkzxl.log.aspect;

import cn.hutool.core.text.StrFormatter;
import com.github.sparkzxl.core.utils.AspectUtil;
import com.github.sparkzxl.core.utils.ListUtils;
import com.github.sparkzxl.log.annotation.OptLogRecord;
import com.github.sparkzxl.log.entity.OptLogRecordDetail;
import com.github.sparkzxl.log.store.ILogRecordService;
import com.github.sparkzxl.log.store.IOperatorService;
import com.google.common.collect.Lists;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

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
                .setCategory(optLogRecord.category())
                .setDetail(formatLogContent)
                .setUserId(operatorService.getUserId())
                .setOperator(operatorService.getUserName());
        CompletableFuture.runAsync(() -> {
            log.info("操作人【{}】：操作日志：【{}】", logRecord.getOperator(), logRecord.getDetail());
            logRecordService.record(logRecord);
        }, threadPoolExecutor);
        return joinPoint.proceed();
    }
}
