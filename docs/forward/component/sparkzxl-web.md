# sparkzxl-web-starter

> 职能：
> 统一API请求封装，在业务系统中只需要一个注解ResponseResult，便可以对返回结果进行包装返回

## POM

```xml

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <!-- undertow web服务器 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-undertow</artifactId>
    </dependency>
    <dependency>
        <groupId>com.github.sparkzxl</groupId>
        <artifactId>sparkzxl-core</artifactId>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>com.github.sparkzxl</groupId>
        <artifactId>sparkzxl-cache-starter</artifactId>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

## 功能

- 替换tomcat服务，使用undertow替代，使用更快，更高效
- 统一API请求封装，在业务系统中只需要一个注解ResponseResult，便可以对返回结果进行包装返回
- 统一异常处理，使用全局api返回格式输出异常结果
- 同时使用断言式异常，隐性抛出异常，消除异常对业务代码的污染
- jackson 处理时间，枚举，Long类型的精度丢失问题

## 使用方法

1. 引入依赖

```xml

<dependency>
    <groupId>com.github.sparkzxl</groupId>
    <artifactId>sparkzxl-web-starter</artifactId>
    <version>${sparkzxl.version}</version>
</dependency>
```

2. 在controller层加入@ResponseResult注解，可加在方法上，也可加在类上

![20200918100241.png](https://oss.sparksys.top/https://oss.sparksys.top/sparkzxl-component/20200918100241.png)

3. 异常的处理

```java
ResponseResultStatus.ACCOUNT_EMPTY.assertNotNull(authUserDetail);
```

默认有常见的异常枚举类

```java

@Getter
@AllArgsConstructor
public enum ResponseResultStatus implements BusinessEnumSysAssert {

    /**
     * 操作成功
     */
    SUCCESS(HttpStatus.HTTP_OK, "操作成功"),

    /**
     * 业务异常
     */
    FAILURE(HttpStatus.HTTP_BAD_REQUEST, "哎呀，开了个小差，请稍后再试"),

    /**
     * 请求未授权
     */
    UN_AUTHORIZED(HttpStatus.HTTP_UNAUTHORIZED, "暂未登录或token已过期"),

    AUTHORIZED_FAIL(HttpStatus.HTTP_UNAUTHORIZED, "授权失败，请重新尝试"),

    USERNAME_EMPTY(HttpStatus.HTTP_UNAUTHORIZED, "用户名不能为空"),

    PASSWORD_EMPTY(HttpStatus.HTTP_UNAUTHORIZED, "密码不能为空"),

    PASSWORD_ERROR(HttpStatus.HTTP_UNAUTHORIZED, "密码不正确"),

    ACCOUNT_EMPTY(HttpStatus.HTTP_UNAUTHORIZED, "账户不存在"),

    UN_PERMISSION(HttpStatus.HTTP_UNAUTHORIZED, "抱歉，您没有访问权限"),
    /**
     * 404 没找到请求
     */
    NOT_FOUND(HttpStatus.HTTP_NOT_FOUND, "404 没找到请求"),

    /**
     * 消息不能读取
     */
    MSG_NOT_READABLE(HttpStatus.HTTP_BAD_REQUEST, "消息不能读取"),

    /**
     * 不支持当前请求方法
     */
    METHOD_NOT_SUPPORTED(HttpStatus.HTTP_BAD_METHOD, "不支持当前请求方法"),

    /**
     * 不支持当前媒体类型
     */
    MEDIA_TYPE_NOT_SUPPORTED(HttpStatus.HTTP_UNSUPPORTED_TYPE, "不支持当前媒体类型"),

    /**
     * 服务器异常
     */
    INTERNAL_SERVER_ERROR(HttpStatus.HTTP_INTERNAL_ERROR, "系统繁忙，请稍候再试"),

    /**
     * 缺少必要的请求参数
     */
    PARAM_MISS(HttpStatus.HTTP_BAD_REQUEST, "缺少必要的请求参数"),

    /**
     * 请求参数类型错误
     */
    PARAM_TYPE_ERROR(HttpStatus.HTTP_BAD_REQUEST, "请求参数类型错误"),

    /**
     * 请求参数绑定错误
     */
    PARAM_BIND_ERROR(HttpStatus.HTTP_BAD_REQUEST, "请求参数绑定错误"),

    /**
     * 参数校验失败
     */
    PARAM_VALID_ERROR(HttpStatus.HTTP_BAD_REQUEST, "参数校验失败"),

    MUCH_KILL(HttpStatus.HTTP_INTERNAL_ERROR, "哎呦喂，人也太多了，请稍后！"),

    SUCCESS_KILL(HttpStatus.HTTP_OK, "秒杀成功"),

    END_KILL(HttpStatus.HTTP_BAD_REQUEST, "秒杀结束"),

    TOO_MUCH_DATA_ERROR(HttpStatus.HTTP_INTERNAL_ERROR, "批量新增数据过多"),

    SERVICE_MAPPER_ERROR(-11, "Mapper类转换异常"),

    SERVICE_DEGRADATION(HttpStatus.HTTP_UNAVAILABLE, "服务降级，请稍候再试"),

    UPLOAD_FAILURE(HttpStatus.HTTP_INTERNAL_ERROR, "上传文件失败了哦"),

    /**
     * 请求被拒绝
     */
    REQ_REJECT(HttpStatus.HTTP_FORBIDDEN, "请求被拒绝"),

    /**
     * 请求次数过多
     */
    REQ_LIMIT(1001, "单位时间内请求次数过多，请稍后再试"),

    /**
     * 黑名单
     */
    REQ_BLACKLIST(1002, "IP已被禁止，请稍后再试"),

    /**
     * 黑名单
     */
    PARAM_FLOW(1003, "热点参数访问限制，请稍后再试"),

    /**
     * token签名不合法
     */
    TOKEN_VALID_ERROR(HttpStatus.HTTP_BAD_REQUEST, "token校验失败"),

    /**
     * token已过期
     */
    TOKEN_EXPIRED_ERROR(HttpStatus.HTTP_BAD_REQUEST, "token已过期");

    final int code;

    final String message;
}
```

断言可使用的场景(判断是否为空，判断是否为真，判断比较大小三种情况)

```java
public interface SparkZxlAssert {
    /**
     * 创建异常
     *
     * @param args 入参
     * @return BaseException
     */
    BaseException newException(Object... args);

    /**
     * 创建异常
     *
     * @param t    t
     * @param args 入参
     * @return
     */
    BaseException newException(Throwable t, Object... args);

    /**
     * 断言对象obj是否为True
     *
     * @param obj 入参
     */
    default void assertNotTrue(Boolean obj) {
        if (!obj) {
            throw newException(false);
        }
    }

    /**
     * 断言对象比较大小，start<end 抛出异常
     *
     * @param start start
     * @param end   end
     */
    default void assertCompare(long start, long end) {
        if (start < end) {
            throw newException(-1);
        }
    }

    /**
     * 断言对象obj非空。如果对象obj为空，则抛出异常
     *
     * @param obj 入参
     */
    default void assertNotNull(Object obj) {
        if (obj == null) {
            throw newException(obj);
        }
    }

    /**
     * 断言对象obj非空。如果对象obj为空，则抛出异常
     * 异常信息message支持传递参数方式，避免在判断之前进行字符串拼接操作
     *
     * @param obj  随想
     * @param args message
     */
    default void assertNotNull(Object obj, Object... args) {
        if (obj == null) {
            throw newException(args);
        }
    }

    /**
     * 默认断言异常
     * 常用于处理已知异常
     */
    default void assertException() {
        throw newException();
    }
}
```

那么其他情况如何处理呢,别担心，我通过用静态工厂的方法可自由灵活去处理异常，使用方法

```java
SparkZxlExceptionAssert.businessFail("发生异常")
```

这样，我们再业务代码中就再也看不到异常的存在，对整体的代码可阅读性大大增强

## 思考

> 这样的异常抛出是否合理？对业务不可见，是否会产生不可控的异常

## 总结

使用起来非常方便，对整体的架构，设计出可复用的代码，对架构师显得尤为重要

# 公众号

学习不走弯路，关注公众号「凛冬王昭君」

![wechat-sparkzxl.jpg](https://oss.sparksys.top/sparkzxl-component/wechat-sparkzxl.jpg)
