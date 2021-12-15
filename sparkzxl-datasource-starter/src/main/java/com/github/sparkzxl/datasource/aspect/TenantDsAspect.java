package com.github.sparkzxl.datasource.aspect;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import com.github.sparkzxl.datasource.provider.DataSourceProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;

/**
 * description: 租户数据源AOP拦截
 *
 * @author zhouxinlei
 */
@Aspect
@Slf4j
public class TenantDsAspect {

    private DataSourceProvider dataSourceProvider;
    private DynamicRoutingDataSource dynamicRoutingDataSource;

    @Autowired(required = false)
    public void setDynamicRoutingDataSource(DynamicRoutingDataSource dynamicRoutingDataSource) {
        this.dynamicRoutingDataSource = dynamicRoutingDataSource;
    }

    @Autowired(required = false)
    public void setDataSourceProvider(DataSourceProvider dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    @Pointcut("@within(com.github.sparkzxl.datasource.annotation.TenantDS)|| @annotation(com.github.sparkzxl.datasource.annotation.TenantDS)")
    public void pointCut() {

    }

    /**
     * 环绕操作
     *
     * @param proceedingJoinPoint 切入点
     * @return 原方法返回值
     * @throws Throwable 异常信息
     */
    @Around("pointCut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String tenantId = RequestLocalContextHolder.getTenant();
        log.info("租户数据源标识：{}", tenantId);
        DynamicDataSourceContextHolder.poll();
        log.info("当前租户Id:{}", tenantId);
        if (StringUtils.isNotBlank(tenantId) && !dynamicRoutingDataSource.getDataSources().containsKey(tenantId)) {
            DataSource dataSource = dataSourceProvider.loadSelectedDataSource(tenantId);
            dynamicRoutingDataSource.addDataSource(tenantId, dataSource);
        }
        DynamicDataSourceContextHolder.push(tenantId);
        return proceedingJoinPoint.proceed();
    }

    /**
     * 后置通知
     */
    @AfterReturning("pointCut()")
    public void afterReturning(JoinPoint joinPoint) {
        DynamicDataSourceContextHolder.clear();
    }

    /**
     * 异常通知，拦截记录异常日志
     */
    @AfterThrowing(pointcut = "pointCut()")
    public void doAfterThrow() {
        DynamicDataSourceContextHolder.clear();
    }

}
