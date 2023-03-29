package com.github.sparkzxl.mybatis.plugins;

import com.github.sparkzxl.mybatis.echo.core.EchoServiceImpl;
import com.github.sparkzxl.mybatis.echo.properties.EchoProperties;
import java.sql.Statement;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.springframework.context.ApplicationContext;

/**
 * description: mybatis数据回显插件
 *
 * @author zhouxinlei
 * @since 2022-02-20 18:52:58
 */
@Intercepts({@Signature(
        type = ResultSetHandler.class,
        method = "handleResultSets",
        args = {Statement.class}
)})
@Slf4j
public class EchoDataInterceptor implements Interceptor {

    private final ApplicationContext applicationContext;
    private final EchoProperties echoProperties;

    public EchoDataInterceptor(ApplicationContext applicationContext, EchoProperties echoProperties) {
        this.applicationContext = applicationContext;
        this.echoProperties = echoProperties;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object proceed = invocation.proceed();
        EchoServiceImpl echoServiceImpl = applicationContext.getBean(EchoServiceImpl.class);
        if (echoProperties.getEnabled()) {
            log.debug("查询数据回显注入开始======>");
            echoServiceImpl.action(proceed, echoProperties.getGuavaCache().getEnabled(), ArrayUtils.EMPTY_STRING_ARRAY);
            log.debug("查询数据回显注入结束======>");
        }
        return proceed;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

}
