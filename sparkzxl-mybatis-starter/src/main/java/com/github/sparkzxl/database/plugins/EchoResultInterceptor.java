package com.github.sparkzxl.database.plugins;

import com.github.sparkzxl.database.echo.core.EchoService;
import com.github.sparkzxl.database.echo.properties.EchoProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.*;
import org.springframework.context.ApplicationContext;

import java.sql.Statement;

/**
 * description: mybatis数据回显插件
 *
 * @author zhouxinlei
 * @date 2022-02-20 18:52:58
 */
@Intercepts({@Signature(
        type = ResultSetHandler.class,
        method = "handleResultSets",
        args = {Statement.class}
)})
@Slf4j
public class EchoResultInterceptor implements Interceptor {

    private final ApplicationContext applicationContext;
    private final EchoProperties echoProperties;

    public EchoResultInterceptor(ApplicationContext applicationContext, EchoProperties echoProperties) {
        this.applicationContext = applicationContext;
        this.echoProperties = echoProperties;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object proceed = invocation.proceed();
        EchoService echoService = applicationContext.getBean(EchoService.class);
        if (echoProperties.getEnabled()) {
            log.info("查询数据回显注入开始======>");
            echoService.action(proceed, echoProperties.getGuavaCache().getEnabled(), ArrayUtils.EMPTY_STRING_ARRAY);
            log.info("查询数据回显注入结束======>");
        }
        return proceed;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

}
