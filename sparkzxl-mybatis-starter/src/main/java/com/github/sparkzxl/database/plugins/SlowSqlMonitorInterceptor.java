package com.github.sparkzxl.database.plugins;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;
import com.github.sparkzxl.constant.enums.EnvironmentEnum;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

/**
 * description: 拦截执行时间过长的sql语句并发送钉钉消息
 *
 * @author zhouxinlei
 * @date 2021-09-17 15:21:50
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
                RowBounds.class, ResultHandler.class})
})
@Slf4j
public class SlowSqlMonitorInterceptor implements Interceptor {
    private static final ExecutorService THREAD_POOL = TtlExecutors.getTtlExecutorService(new ThreadPoolExecutor(5, 5, 0, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1000), new ThreadPoolExecutor.DiscardPolicy()));
    private static final TransmittableThreadLocal<String> CONTEXT = new TransmittableThreadLocal<>();
    /**
     * 单次sql查询的检测时间阀值
     */
    private long longQueryTime = 3 * 1000;

    private ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        String environment = StringUtils.isEmpty(applicationContext.getEnvironment().getProperty("spring.profiles.active")) ?
                "dev" : applicationContext.getEnvironment().getProperty("spring.profiles.active");
        if (StringUtils.equals(environment, StringUtils.toRootLowerCase(EnvironmentEnum.GRAY.name()))
                || StringUtils.equals(environment, StringUtils.toRootLowerCase(EnvironmentEnum.PROD.name()))) {
            return;
        }

        // 测试环境加长时间
        this.longQueryTime = 10 * 1000;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object returnValue;
        long executeTime;
        Stopwatch dbStopwatch = Stopwatch.createStarted();
        try {
            returnValue = invocation.proceed();
            executeTime = dbStopwatch.elapsed(TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            doSomething(invocation, e.getCause() == null ? e.getMessage() : e.getCause().getMessage(), Type.SQL_EXCEPTION);
            throw new Throwable(e);
        }
        doSomething(invocation, executeTime, Type.SLOW_SQL);
        return returnValue;
    }

    private void doSomething(Invocation invocation, long executeTime, Type type) {
        doSomething(invocation, executeTime, null, type);
    }

    private void doSomething(Invocation invocation, String exceptionMsg, Type type) {
        doSomething(invocation, 0, exceptionMsg, type);
    }

    /**
     * 根据结果做一些需要的操作
     *
     * @param invocation
     * @param executeTime
     */
    private void doSomething(Invocation invocation, long executeTime, String exceptionMsg, Type type) {

        try {
            //如果是慢sql检测，但是实际执行时间没有超时，不走结果检测
            if (ObjectUtils.nullSafeEquals(type, Type.SLOW_SQL) && executeTime <= longQueryTime) {
                return;
            }

            CONTEXT.set(getStackTrace());
            THREAD_POOL.execute(() -> {
                try {

                    Stopwatch checkTime = Stopwatch.createStarted();

                    MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];

                    Object parameter = null;

                    if (invocation.getArgs().length > 1) {
                        parameter = invocation.getArgs()[1];
                    }

                    String sqlId = mappedStatement.getId();
                    /**
                     * 这个boundSql会将XML里面所有的#{id,jdbcType=BIGINT} 都解析并替换成 ？
                     * select * from x where id = #{id,jdbcType=BIGINT}
                     * 会被解析成 select * from x where id = ？，这个语句就可以用jdbc 的 PrepareStatement 进行编译执行了。
                     * 然后将对应的javaType、jdbcType 保存起来，在执行SQL时传入statement中。
                     *
                     * PreparedStatement statement = connection.prepareStatement(sql)
                     * statement.setLong(1, excel.getUserId());
                     * statement.executeBatch();
                     * connection.commit();
                     */
                    BoundSql boundSql = mappedStatement.getBoundSql(parameter);
                    Configuration configuration = mappedStatement.getConfiguration();
                    /**
                     * 主要耗时在这里，这里会根据parameter，
                     * 将 select * from x where id = ？ 解析成 select * from x where id = 1
                     * 对于 insert into X (1,2) values (?,?),(?,?),(?,?),(?,?)这种批量的SQL，这里的解析会特别慢。
                     * 因为他是挨个挨个把 ？ 替换成对应的参数值。
                     * 最后拼成SQL insert into X (1,2) values (1,2),(3,4)
                     * 如果这个是复杂对象（自定义bean），会根据反射一个个进行操作。
                     */
                    String sql = parseSql(configuration, boundSql);

                    switch (type) {
                        case SLOW_SQL:
                            //检测慢sql
                            checkSlowSql(sqlId, sql, executeTime, checkTime.elapsed(TimeUnit.MILLISECONDS));
                            break;
                        case SQL_EXCEPTION:
                            sendSqlExceptionMsg(sqlId, sql, exceptionMsg, checkTime.elapsed(TimeUnit.MILLISECONDS));
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送异常sql信息
     *
     * @param exceptionMsg
     * @param sqlId
     * @param sql
     */
    private void sendSqlExceptionMsg(String sqlId, String sql, String exceptionMsg, long checkTime) {

        String sqlMsg = String.format("异常sql, 检测耗时：[%d]ms\nSQLId: \n\t %s\n\nSQL语句: \n\t %s \n\n异常信息: \n\t %s \n\n方法调用信息:", checkTime, sqlId, sql, exceptionMsg);

        sendMsg(sqlMsg);
    }

    /**
     * 检测慢sql
     *
     * @param executeTime
     * @param sqlId
     * @param sql
     */
    private void checkSlowSql(String sqlId, String sql, long executeTime, long checkTime) {

        if (executeTime >= longQueryTime) {
            try {
                String sqlMsg = String.format("慢sql, 执行耗时: [%d]ms，检测耗时：[%d]ms\nSQLId: \n\t %s\n\nSQL语句: \n\t %s \n\n方法调用信息:", executeTime, checkTime, sqlId, sql);
                sendMsg(sqlMsg);
            } catch (Exception e) {
                log.error("发送钉钉消息失败", e);
            }
        }
    }

    /**
     * 发送钉钉消息
     *
     * @param msg
     */
    private void sendMsg(String msg) {
        msg += CONTEXT.get();
        log.warn(msg);
        //dingTalkMessageUtils.sendTextMessage(msg, DingTalkMessageUtils.Type.SLOW_SQL);
    }

    /**
     * 获取当前方法的调用方法
     *
     * @return 方法调用
     */
    private String getStackTrace() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        if (ArrayUtils.isEmpty(elements) || elements.length < 5) {
            return StringUtils.EMPTY;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 4; i < elements.length; i++) {
            StackTraceElement stackTraceElement = elements[i];
            if (Objects.isNull(stackTraceElement)) {
                continue;
            }

            String className = stackTraceElement.getClassName();
            if (Strings.isNullOrEmpty(className)) {
                continue;
            }

            // 防止打印信息过多
            if (!className.startsWith("com.raycloud")) {
                continue;
            }

            // 过滤当前方法
            if (className.equals("com.raycloud.qnee.idpt.commons.plugins.SlowSqlMonitor.intercept")) {
                continue;
            }

            sb.append("\t ")
                    .append(className)
                    .append(".")
                    .append(stackTraceElement.getMethodName())
                    .append("(")
                    .append(stackTraceElement.getFileName())
                    .append(":")
                    .append(stackTraceElement.getLineNumber())
                    .append(")")
                    .append("\n");
        }

        return sb.toString();
    }

    /**
     * 将对象转换为字符串
     *
     * @param obj 对象
     * @return 字符串
     */
    private static String getParameterValue(Object obj) {
        if (obj instanceof String) {
            return "'" + obj.toString() + "'";
        }

        if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            return "'" + formatter.format(new Date()) + "'";
        }
        if (Objects.isNull(obj)) {
            return StringUtils.EMPTY;
        }

        return obj.toString();
    }

    /**
     * 解析sql语句
     *
     * @param configuration mybatis配置信息
     * @param boundSql      mybatis存放sql信息的对象
     * @return 实际执行的sql语句
     */
    private String parseSql(Configuration configuration, BoundSql boundSql) {
        // 传入的参数
        Object parameterObject = boundSql.getParameterObject();

        // 替换多个空格为一个
        String anyBlankRegex = "[\\s]+";
        String sql = boundSql.getSql().replaceAll(anyBlankRegex, " ");

        // 判断传入的参数是否为空
        if (Objects.isNull(parameterObject)) {
            return sql;
        }

        // 获取实际使用的sql参数
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();

        if (CollectionUtils.isEmpty(parameterMappings)) {
            return sql;
        }

        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
            // 替换问号
            sql = replaceFirstParameter(sql, parameterObject);
            return sql;
        }

        // 获取mybatis的缓存反射对象
        MetaObject metaObject = configuration.newMetaObject(parameterObject);
        for (ParameterMapping parameterMapping : parameterMappings) {
            String propertyName = parameterMapping.getProperty();
            if (metaObject.hasGetter(propertyName)) {
                Object obj = metaObject.getValue(propertyName);
                if (Objects.isNull(obj)) {
                    continue;
                }
                sql = replaceFirstParameter(sql, obj);
            } else if (boundSql.hasAdditionalParameter(propertyName)) {
                Object obj = boundSql.getAdditionalParameter(propertyName);
                if (Objects.isNull(obj)) {
                    continue;
                }
                sql = replaceFirstParameter(sql, obj);
            }
        }

        return sql;
    }

    /**
     * 替换sql语句中的第一个?为实际参数
     *
     * @param sql 传入的sql
     * @param obj 参数
     * @return 替换后的语句
     */
    private String replaceFirstParameter(String sql, Object obj) {
        // 修复Illegal group reference的Bug,对特殊字符添加转义字符
        return sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
    }

    enum Type {
        SLOW_SQL,
        SQL_EXCEPTION
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
