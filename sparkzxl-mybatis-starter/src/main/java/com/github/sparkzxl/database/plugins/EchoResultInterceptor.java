package com.github.sparkzxl.database.plugins;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.sparkzxl.annotation.echo.EchoField;
import com.github.sparkzxl.core.util.StrPool;
import com.github.sparkzxl.database.echo.core.EchoService;
import com.github.sparkzxl.database.echo.manager.ClassManager;
import com.github.sparkzxl.database.echo.manager.FieldParam;
import com.github.sparkzxl.database.echo.manager.LoadKey;
import com.github.sparkzxl.database.echo.properties.EchoProperties;
import com.github.sparkzxl.entity.data.RemoteData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
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
            echoService.action(proceed, ArrayUtils.EMPTY_STRING_ARRAY);
            log.info("查询数据回显注入结束======>");
        }
        return proceed;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

}
