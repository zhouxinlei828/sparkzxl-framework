# 1 场景分析

## 1.1 情景再现

在微服务中经常会出现这样一个场景，比如我有一个服务`admin`，里面有着获取用户信息的接口，我有一个服务`service-a`，需要用户信息，这时我需要通过 `service-a` 去调用 `admin` 服务的接口来获得用户信息

通过 `Feign`，我们不需要自己来写个http请求发送获取数据，也不需要引用其他服务来获取数据，做到服务间的解耦

Feign 会自动通过轮询的方式去进行负载均衡，且开启Feign的Hystrix支持后就会自动对宕机的服务进行拦截，防止雪崩效应， 但是有这么个场景： 如果admin服务获取用户信息的接口发生了不可预知的异常，那么服务`service-a`
有两种处理策略：

- 其一就是走`hystrix`熔断降级处理：设置feign降级处理策略，响应兜底结果，保证服务正常运行
- 其二就是全局进行异常处理，但是如果服务`admin`设置了兜底异常的话，返回的结果就无法进行解析，举个例子，统一响应结果，在接口层统一API接口响应结构体，例如：

```Json
{
  "code": 200,
  "success": true,
  "msg": "操作成功",
  "data": {
  }
}
```

但是这样，需要服务`admin`接口响应提供API接口响应结构体，在服务`service-a`进行判断响应接口结果，再进行合理的抛出响应上游异常结果

> 在读的场景下，这种可以第一种和第二种方案都可以满足 但是在写入数据的情况下第一种和第二种都无法满足；

## 1.2 为什么写入场景降级无法满足呢？

- 在写入服务`admin`的时候，服务`admin`遇到了不可预知的异常，导致写入数据失败，但是在服务`service-a`无法捕获服务`admin`产生的异常，无法进行服务`service-a`的回滚业务

- 你会说第二种可以满足，但是第二种的缺点就是你得需要判断服务`admin`的响应结果，返回异常，回滚`service-a`，假如有这么个场景，先写入服务`admin`，服务`admin`写入成功，`service-a`
  写入失败，这样无法回滚服务`admin`的数据；这种场景其实已经是分布式事务的场景，这边我们不做具体细化，调用步骤如图：

![服务调用步骤.jpg](https://oss.sparksys.top/halo/%E6%9C%8D%E5%8A%A1%E8%B0%83%E7%94%A8%E6%AD%A5%E9%AA%A4_1627628483917.jpg)

> 重点就是：我们能否捕获服务之间的异常？ 答案：当然是可以的

## 1.3 服务之间的异常如何进行传递？

> 如何利用现有的spring cloud openfeign框架，优雅的进行服务之间的异常传递

在springcloud中 服务与服务之间,通常使用feign进行服务调用。但是在fei中，默认返回feign包装后的异常。eg:
如果服务a调用服务b，当服务b发生异常时，如果什么都不处理的话，将抛出feign自带的异常，结合Hystrix，异常最终都会被Hystrix`吃掉`
某些场景需要走服务降级处理，但是某些场景需要讲异常进行传递

我的想法是**如果调用服务A时发生请求异常，服务B返回的能够返回服务A抛出的异常**

## 1.4 解决思路

1. 通过网上一些资料的查询，看到很多文章会`HystrixBadRequestException`不会触发 hystrix 的熔断 –> 但是并没有介绍该异常的实践方案
2. 感觉要解决项目的痛点，切入点应该就在`HystrixBadRequestException` 了。于是先看源码，一方面对 Hystrix 加深理解，尝试理解作者设计的初衷与想法，另一方面看看是否能找到其他方案达到较高的实践标准

- Fallback fallback 是 Hystrix 命令执行失败时使用的后备方法，用来实现服务的降级处理逻辑。在 HystrixCommand 中可以通过重载 getFallback() 方法来实现服务降级逻辑，Hystrix 会在
  run() 执行过程中出现错误、超时、线程池拒绝、短路熔断等情况时，执行 getFallback() 方法内的逻辑。 通常，当 HystrixCommand 的主方法（run()） 中抛出异常时，便会触发 getFallback()
  。除了一个例外 —— HystrixBadRequestException。当抛出 HystrixBadRequestException，不论当前 Command 是否定义了 getFallback()，都不会触发，而是向上抛出异常。
  如果实现业务时有一些异常希望能够向上抛出，而不是触发 Fallback 策略，便可以封装到 HystrixBadRequestException 中。 getFallback() 的执行时间并不受 HystrixCommand
  的超时时间的控制。

- Feign对异常的封装 通过实现FallbackFactory,可以在create方法中获取到服务抛出的异常。但是请注意，这里的异常是被Feign封装过的异常，不能直接在异常信息中看出原始方法抛出的异常。

# 2 代码实现

**继承DefaultErrorAttributes，进行异常格式转码处理，实现ErrorDecoder，进行解码异常信息，抛出RemoteCallException传递异常信息**

> 针对熔断降级和feign自身的异常进行捕获异常传导的适配

## 2.1 `FeignErrorResult` feign异常结果类

```Java
package com.github.sparkzxl.model.exception;

import cn.hutool.http.HttpStatus;
import lombok.Data;

import java.util.List;

@Data
public class FeignErrorResult {
    private int code;
    private boolean success;
    private String msg;
    private List<ExceptionChain> exceptionChains;

    /**
     * 返回结果
     *
     * @param code 状态码
     * @param msg  信息
     * @return FeignResult
     */
    public static FeignErrorResult feignErrorResult(int code, String msg, List<ExceptionChain> exceptionChains) {
        FeignErrorResult feignErrorResult = new FeignErrorResult();
        feignErrorResult.setCode(code);
        feignErrorResult.setMsg(msg);
        feignErrorResult.setExceptionChains(exceptionChains);
        feignErrorResult.setSuccess(code == HttpStatus.HTTP_OK);
        return feignErrorResult;
    }

}

```

## 2.2 `DefaultErrorAttributes`对发生的异常进行处理

```Java
package com.github.sparkzxl.feign.default_;

import com.github.sparkzxl.constant.ExceptionConstant;
import com.github.sparkzxl.core.jackson.JsonUtil;
import com.github.sparkzxl.core.utils.ResponseResultUtils;
import com.github.sparkzxl.feign.config.FeignExceptionHandlerContext;
import com.github.sparkzxl.feign.exception.RemoteCallException;
import com.github.sparkzxl.model.exception.ExceptionChain;
import com.github.sparkzxl.model.exception.FeignErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * description: 当服务内报错 返回给Feign的处理器
 *
 * @author zhouxinlei
 */
@Slf4j
public class FeignExceptionHandler extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);
        Throwable error = super.getError(webRequest);
        List<ExceptionChain> exceptionChains = null;
        if (error instanceof RemoteCallException) {
            exceptionChains = ((RemoteCallException) error).getExceptionChains();
        } else {
            Object attribute = webRequest.getAttribute(ExceptionConstant.EXCEPTION_CHAIN_KEY, RequestAttributes.SCOPE_REQUEST);
            if (attribute != null) {
                exceptionChains = JsonUtil.parseArray(attribute.toString(), ExceptionChain.class);
            }
            if (exceptionChains == null) {
                exceptionChains = new ArrayList<>(1);
            }
        }
        String message = error.getCause().getMessage();
        ExceptionChain exceptionChain = new ExceptionChain();
        exceptionChain.setMsg(message);
        exceptionChain.setPath(errorAttributes.get("path").toString());
        exceptionChain.setTimestamp(new Date());
        exceptionChain.setApplicationName(FeignExceptionHandlerContext.getApplicationName());
        //添加发生的异常类信息 以便下一步处理
        if (error.getClass() != null) {
            exceptionChain.setExceptionClass(error.getClass().getTypeName());
        }
        exceptionChains.add(exceptionChain);
        Integer status = (Integer) errorAttributes.get("status");
        ResponseResultUtils.clearResponseResult();
        return response(status, message, exceptionChains);
    }


    /**
     * 构建返回的JSON数据格式
     *
     * @param status       状态码
     * @param errorMessage 异常信息
     * @return Map<String, Object>
     */
    public static Map<String, Object> response(int status, String errorMessage, List<ExceptionChain> exceptionChains) {
        FeignErrorResult feignErrorResult = FeignErrorResult.feignErrorResult(status, errorMessage, exceptionChains);
        log.error("feign 请求拦截异常：[{}]", JsonUtil.toJson(feignErrorResult));
        return JsonUtil.toMap(feignErrorResult);
    }
}


```

## 2.3 新建远程调用异常`RemoteCallException`，用于服务之间的异常传递

```Java

import cn.hutool.core.date.DatePattern;
import com.github.sparkzxl.core.utils.DateUtils;
import com.github.sparkzxl.feign.config.FeignExceptionHandlerContext;
import com.github.sparkzxl.model.exception.ExceptionChain;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * description: feign远程调用异常
 *
 * @author zhouxinlei
 */
@Slf4j
public class RemoteCallException extends RuntimeException {

    private final List<StackTraceElement> stackTraceElements = new ArrayList<>(2);

    private boolean isAddThis = false;

    @Override
    public StackTraceElement[] getStackTrace() {
        if (stackTraceElements.isEmpty()) {
            return super.getStackTrace();
        }
        return stackTraceElements.toArray(new StackTraceElement[0]);
    }

    @Getter
    private List<ExceptionChain> exceptionChains;


    public RemoteCallException(String message) {
        super(message);
    }

    public RemoteCallException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteCallException(String message, List<ExceptionChain> exceptionChains) {
        super(message);
        this.exceptionChains = exceptionChains;
        if (CollectionUtils.isNotEmpty(exceptionChains)) {
            for (int i = 0; i < exceptionChains.size(); i++) {
                String status = i == 0 ? "HAPPEN" : "THROW";
                this.create(exceptionChains.get(i), status);
            }
        }

    }

    /**
     * 获取原始异常信息
     */
    public String getRawMessage() {
        ExceptionChain rawExceptionInfo = this.getRawExceptionInfo();
        return rawExceptionInfo == null ? null : rawExceptionInfo.getMsg();
    }

    public ExceptionChain getRawExceptionInfo() {
        return CollectionUtils.isEmpty(exceptionChains) ? null : exceptionChains.get(0);
    }

    /**
     * 判断异常是否为原始异常的子类
     *
     * @param exception 异常
     * @return boolean
     */
    public boolean isAssignableFrom(Class<? extends Throwable> exception) {
        ExceptionChain rawExceptionInfo = this.getRawExceptionInfo();
        return rawExceptionInfo != null && rawExceptionInfo.isAssignableFrom(exception);
    }

    @Override
    public String toString() {
        if (!isAddThis) {
            this.addThis();
            isAddThis = true;
        }
        return super.toString();
    }

    @Override
    public void printStackTrace() {
        if (!isAddThis) {
            this.addThis();
            isAddThis = true;
        }
        PrintStream err = System.err;
        err.println("cn.minsin.feign.exception.RemoteCallException : " + this.getMessage());
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            err.println("\t" + stackTraceElement);
        }
    }

    private void create(ExceptionChain exceptionChain, String status) {
        String format = "[%s]:[%s] timestamp:'%s',exceptionClass:'%s',message:'%s',path: '%s'";
        String str = String.format(format,
                status,
                exceptionChain.getApplicationName(),
                DateUtils.format(exceptionChain.getTimestamp(), DatePattern.NORM_DATETIME_MS_FORMAT),
                exceptionChain.getExceptionClass(),
                exceptionChain.getMsg(),
                exceptionChain.getPath()
        );
        StackTraceElement stackTraceElement = new StackTraceElement(
                str, "", "", 0
        );
        this.stackTraceElements.add(stackTraceElement);
    }

    private void addThis() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String requestPath = "";
        if (requestAttributes instanceof ServletRequestAttributes) {
            requestPath = ((ServletRequestAttributes) requestAttributes).getRequest().getRequestURI();
        }
        ExceptionChain exceptionChain = new ExceptionChain();
        exceptionChain.setApplicationName(FeignExceptionHandlerContext.getApplicationName());
        exceptionChain.setPath(requestPath);
        exceptionChain.setTimestamp(new Date());
        exceptionChain.setExceptionClass(RemoteCallException.class.getTypeName());
        exceptionChain.setMsg(this.getMessage());
        this.create(exceptionChain, "END");
    }

}
```

## 2.4 实现feign ErrorDecoder，进行异常处理捕获

作用：异常解析器,需要实现ErrorDecoder。作用是,当feign服务调用其他服务出现异常,收到的异常数据流处理类.

```Java

package com.github.sparkzxl.feign.default_;

import com.github.sparkzxl.core.jackson.JsonUtil;
import com.github.sparkzxl.feign.exception.RemoteCallException;
import com.github.sparkzxl.model.exception.FeignErrorResult;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * description: 当调用远程服务 其抛出异常捕获
 *
 * @author zhouxinlei
 */
@Slf4j
public class FeignExceptionDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            Reader reader = response.body().asReader(StandardCharsets.UTF_8);
            String body = Util.toString(reader);
            FeignErrorResult feignErrorResult = JsonUtil.parse(body, FeignErrorResult.class);
            return new RemoteCallException(feignErrorResult.getMsg(), feignErrorResult.getExceptionChains());
        } catch (Exception e) {
            log.error("[{}] has an unknown exception.", methodKey, e);
            return new RemoteCallException("unKnowException", e);
        }

    }
}

```

## 2.5 新建FeignExceptionHandlerContext，静态spring常量容器

```Java

package com.github.sparkzxl.feign.config;

import org.springframework.core.env.Environment;

/**
 * description:
 *
 * @author zhouxinlei
 */
public final class FeignExceptionHandlerContext {


    private static Environment ENVIRONMENT;


    public static String getApplicationName() {
        return ENVIRONMENT == null ? "unknownServer" : ENVIRONMENT.getProperty("spring.application.name");
    }

    public static Environment getEnvironment() {
        return ENVIRONMENT;
    }

    public static void setEnvironment(Environment environment) {
        if (ENVIRONMENT == null) {
            FeignExceptionHandlerContext.ENVIRONMENT = environment;
        }
    }
}

```

说明:这个类,在注入时会将当前环境存放进去,但是只能赋值一次。通过Environment 可以获取到yaml或properties中的配置,默认提供获取application name的方法。

## 2.6 新建EnableFeignExceptionHandler注解

```Java

import com.github.sparkzxl.feign.config.RegistryFeignExceptionHandler;
import com.github.sparkzxl.feign.default_.FeignExceptionDecoder;
import com.github.sparkzxl.feign.default_.FeignExceptionHandler;
import feign.codec.ErrorDecoder;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * description:
 *
 * @author zhouxinlei
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({RegistryFeignExceptionHandler.class})
public @interface EnableFeignExceptionHandler {

    /**
     * 异常抛出处理类, 必须要有无参构造方法
     *
     * @return Class<? extends ErrorAttributes>
     */
    Class<? extends ErrorAttributes> handlerClass() default FeignExceptionHandler.class;

    /**
     * 异常解析处理类, 必须要有无参构造方法
     *
     * @return Class<? extends ErrorDecoder>
     */
    Class<? extends ErrorDecoder> decoderClass() default FeignExceptionDecoder.class;

}

```

## 2.7 新建 RegistryFeignExceptionHandler处理类，进行feign异常处理

```Java

package com.github.sparkzxl.feign.config;

import com.github.sparkzxl.feign.annoation.EnableFeignExceptionHandler;
import feign.codec.ErrorDecoder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Objects;

import static org.springframework.beans.factory.config.AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;

/**
 * description: feign异常处理
 *
 * @author zhouxinlei
 */
@Slf4j
public class RegistryFeignExceptionHandler implements ImportBeanDefinitionRegistrar, EnvironmentAware, Ordered {

    @Override
    @SneakyThrows
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {

        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(EnableFeignExceptionHandler.class.getName()));
        Class<? extends ErrorDecoder> decoderClass = annotationAttributes.getClass("decoderClass");
        ErrorDecoder errorDecoder = BeanUtils.instantiateClass(decoderClass);

        AbstractBeanDefinition decoder = BeanDefinitionBuilder
                .genericBeanDefinition(ErrorDecoder.class, () -> errorDecoder)
                .setAutowireMode(AUTOWIRE_BY_TYPE)
                .getBeanDefinition();
        registry.registerBeanDefinition(Objects.requireNonNull(decoder.getBeanClassName()), decoder);

        Class<? extends ErrorAttributes> handlerClass = annotationAttributes.getClass("handlerClass");

        if (ObjectUtils.isNotEmpty(handlerClass)) {
            ErrorAttributes errorAttributes = BeanUtils.instantiateClass(handlerClass);
            AbstractBeanDefinition handler = BeanDefinitionBuilder
                    .genericBeanDefinition(ErrorAttributes.class, () -> errorAttributes)
                    .setAutowireMode(AUTOWIRE_BY_TYPE)
                    .getBeanDefinition();
            registry.registerBeanDefinition(Objects.requireNonNull(handler.getBeanClassName()), handler);
            boolean infoEnabled = log.isInfoEnabled();
            if (infoEnabled) {
                log.info("[{}] and [{}] has been successfully registered", handler.getBeanClassName(), decoder.getBeanClassName());
            }
        }
    }


    @Override
    public void setEnvironment(Environment environment) {
        //get the application name of project
        FeignExceptionHandlerContext.setEnvironment(environment);
    }

    @Override
    public int getOrder() {
        return 88;
    }
}

```

# 3 使用

在spring boot启动类加上`@EnableFeignExceptionHandler`，设置异常抛出处理类,异常解析处理类，也可使用默认的

## 3.1 无降级测试

1. 文件服务抛出异常

![image.png](https://oss.sparksys.top/halo/image_1627630454554.png)

2. 文件服务feign接口

![image.png](https://oss.sparksys.top/halo/image_1627630552399.png)

3. 在admin服务进行调用，进行捕获异常

![image.png](https://oss.sparksys.top/halo/image_1627633070027.png)

4. 测试结果

- 文件服务截图

![image.png](https://oss.sparksys.top/halo/image_1627630677691.png)

- admin服务截图

![image.png](https://oss.sparksys.top/halo/image_1627632902653.png)

## 3.2 有熔断降级测试

- admin服务feign接口添加降级工厂

![image.png](https://oss.sparksys.top/halo/image_1627633260188.png)

- 降级处理策略

![image.png](https://oss.sparksys.top/halo/image_1627633337872.png)

- 在admin服务进行调用

- 前端结果响应

![image.png](https://oss.sparksys.top/halo/image_1627635271152.png)

# 4. 代码地址

[sparkzxl-feign-starter](https://gitee.com/AbsolutelyNT/sparkzxl-component/tree/nexus/sparkzxl-feign-starter/src/main/java/com/github/sparkzxl/feign)
