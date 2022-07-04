# 1 服务雪崩，服务容错

## 1.1 服务雪崩

在微服务的架构体系中，我们会将系统拆分成多个服务小单元，通过 HTTP 或者 RPC 进行远程调用。如下图所示
![image](https://oss.sparksys.top/halo/image.png)
在绝大多数情况下，服务消费者都能正常的远程调用服务提供者。但是某一时刻，服务提供者执行逻辑**较慢**，又或者网络出现**抖动**的情况，导致服务消费调用服务提供者**超时**或者**失败**。如下图所示：
![image-1656636760018](https://oss.sparksys.top/halo/image-1656636760018.png)
如果这个情况持续一段时间，服务提供者的响应一直**很慢**，导致服务消费者的响应也跟着**很慢**，最终引起服务消费者的请求任务**积压**，也跟着一起出问题了。如下图所示：
![image-1656636861632](https://oss.sparksys.top/halo/image-1656636861632.png)

这种因为一个**下游**服务的故障，导致**上游**服务一起跟着故障的现象，我们称为“**服务雪崩**”。

## 1.2 服务容错

针对“**服务雪崩**”的情况，我们需要进行“服务容错”处理。解决的方向很“简单”，尽量不要去调用故障的服务，避免被拖垮。一般常用的手段有，主要是限流和开关。
![雪崩效应](https://oss.sparksys.top/halo/%E9%9B%AA%E5%B4%A9%E6%95%88%E5%BA%94.jpg)

- 限流
  通过**限制**调用服务的**频率**，避免**频繁**调用故障服务，导致请求任务积压而自身雪崩。

- 开关
  通过关闭对故障服务的调用，停止调用故障服务，从而避免服务雪崩。当然，关闭的前提是，不调用故障服务的情况下，业务逻辑依然可以走下去，或者业务数据的完整性不会被破坏。
  **一般来说，开关会分成手动开关和自动开关**。手动开关比较好了解，自动开关是满足指定条件自动进行关闭。
  自动开关比较经典的就是“**断路器模式**”，它源于 Martin Fowler 大佬在 [《CircuitBreaker》](https://martinfowler.com/bliki/CircuitBreaker.html) 文章的分享。
  > “断路器”，又称自动开关，它是一种既有手动开关作用，**又能自动进行失压、欠压、过载、和短路保护的电器。**
  它可用来分配电能，不频繁地启动异步电动机，对电源线路及电动机等实行保护，**当它们发生严重的过载或者短路及欠压等故障时能自动切断电路，其功能相当于熔断器式开关与过欠热继电器等的组合。** 而且在分断故障电流后一般不需要变更零部件，一获得了广泛的应用

在微服务架构中，“断路器模式”的用途也是类似的。当某个**服务提供者**发生故障（相当于电器发生短路的情况）时，断路器一旦监控到这个情况，会将**开关**进行自动**关闭**。之后，在服务消费者调用该故障服务提供者时，直接抛出错误异常，**不进行调用**
，从而避免调用服务的漫长等待。

# 2 resilience4j

## 2.1简介

> **Resilience4j** 是一个轻量级的容错组件，其灵感来自于 **Hystrix**，但主要为 Java 8 和函数式编程所设计。
> 轻量级体现在其只用 **Vavr** 库，没有任何外部依赖。而 Hystrix 依赖了 Archaius，Archaius 本身又依赖很多第三方包，例如 Guava、Apache Commons Configuration 等等。
> Resilience4j 提供了高阶函数（**装饰器**），以通过断路器、速率限制器、重试或隔板来增强任何功能接口、lambda 表达式或方法引用。您可以在任何功能接口、**lambda 表达式**或方法引用上堆叠多个装饰器。优点是你可以选择你需要的装饰器。

## 2.2 核心模块

|模块|名称|说明|
| ----| ----| ----|
|resilience4j-circuitbreaker|Circuit breaking 熔断器|异常请求熔断防止多个微服务服务持续崩溃|
|resilience4j-ratelimiter| Rate limiting 限流|限流可以认为服务降级的一种，限流就是限制系统的输入和输出流量已达到保护系统的目的|
|resilience4j-bulkhead|Bulkheading 舱壁隔离|Resilience4j 提供了两种可用于限制并发执行数量的隔板模式实现，适用于各种线程和 I/O 模型|
|resilience4j-retry|Automatic retrying (sync and async) 自动重试（同步和异步）|针对请求异常响应设置重试机制
|

# 3 CircuitBreaker熔断器

## 3.1 概述

CircuitBreaker 一共有  **CLOSED**、**OPEN**、**HALF_OPEN**  三种状态，通过状态机实现。转换关系如下图所示：

![image-1656639219445](https://oss.sparksys.top/halo/image-1656639219445.png)

- 当熔断器关闭(**CLOSED**)时，所有的请求都会通过熔断器。
- 如果失败率超过设定的阈值，熔断器就会从关闭状态转换到打开(**OPEN**)状态，这时所有的请求都会被拒绝。
- 当经过一段时间后，熔断器会从打开状态转换到半开(**HALF_OPEN**)状态，这时仅有一定数量的请求会被放入，并重新计算失败率。如果失败率超过阈值，则变为打开(**OPEN**)状态；如果失败率低于阈值，则变为关闭(**CLOSE**)状态。

除此以外，熔断器还会有两种特殊状态：**DISABLED**（始终允许访问）和 **FORCED_OPEN**（始终拒绝访问）。这两个状态不会生成熔断器事件（除状态装换外），并且不会记录事件的成功或者失败。退出这两个状态的唯一方法是触发状态转换或者重置熔断器。

## 3.2 CircuitBreaker实现

> Resilience4j 记录请求状态的数据结构和 <code>Hystrix</code> 不同：Hystrix 是使用滑动窗口来进行存储的，而 Resilience4j 采用的是 <code>Ring Bit Buffer(环形缓冲区)</code>。Ring Bit
> Buffer 在内部使用 <code>BitSet</code> 这样的数据结构来进行存储，结构如下图所示：

![image-1656655396361](https://oss.sparksys.top/halo/image-1656655396361.png)

- 每一次请求的成功或失败状态只占用一个 bit 位，与 boolean 数组相比更节省内存。BitSet 使用 long[] 数组来存储这些数据，意味着 16 个值(64 bit)的数组可以存储 1024 个调用状态。计算失败率需要填满环形缓冲区，例如，如果环形缓冲区的大小为
  10，则必须至少请求满 10 次，才会进行故障率的计算。如果仅仅请求了 9 次，即使 9 个请求都失败，熔断器也不会打开。但是 <code>CLOSE</code> 状态下的缓冲区大小设置为 10 并不意味着只会进入 10 个请求，在熔断器打开之前的所有请求都会被放入。

- 当故障率高于设定的阈值时，熔断器状态会从由 <code>CLOSE</code> 变为 <code>OPEN</code>。这时所有的请求都会抛出CallNotPermittedException 异常。
- 当经过一段时间后，熔断器的状态会从 <code>OPEN</code> 变为 <code>HALF_OPEN</code>。<code>HALF_OPEN</code> 状态下同样会有一个 Ring Bit Buffer，用来计算HALF_OPEN
  状态下的故障率。如果高于配置的阈值，会转换为 <code>OPEN</code>，低于阈值则装换为 <code>CLOSE</code>。与 <code>CLOSE</code> 状态下的缓冲区不同的地方在于，<code>HALF_OPEN</code>
  状态下的缓冲区大小会限制请求数，只有缓冲区大小的请求数会被放入。

除此以外，熔断器还会有两种特殊状态：<code>DISABLED</code>（始终允许访问）和 <code>FORCED_OPEN</code>
（始终拒绝访问）。这两个状态不会生成熔断器事件（除状态装换外），并且不会记录事件的成功或者失败。退出这两个状态的唯一方法是触发状态转换或者重置熔断器。

## 3.2  CircuitBreakerConfig

CircuitBreaker的配置类，在实际项目中除了全局的配置，有些场景需要我们自定义一些CircuitBreaker的配置，Circuitreakeronfig全部属性如下表

| 配置属性                                     | 默认值                                                   | 描述                                                         |
| -------------------------------------------- | -------------------------------------------------------- | ------------------------------------------------------------ |
| waitDurationInOpenState                      | 60000 [毫秒]                                             | 半从打开转换到打开之前应等待的时间。                         |
| slowCallDurationThreshold                    | 60000 [毫秒]                                             | 配置持续时间阈值，该数值的呼叫速度缓慢并增加呼叫的速度。     |
| maxWaitDurationInHalfOpenState               | 0 [毫秒]                                                 | 配置最大等待持续时间，控制断路器在切换到打开状态之前可以保持在半开状态的最长时间。<br/>值 0 表示断路器将在 HalfOpen 状态无限等待，直到所有允许的调用都完成。 |
| failureRateThreshold                         | 50                                                       | 以百分比形式配置失败率阈值。<br/>当故障率等于或大于阈值时，断路器转换为断开并开始短路调用。 |
| slowCallRateThreshold                        | 100                                                      | 以百分比配置阈值。当呼叫持续时间大于 或等于阈值时，断路器将呼叫视为慢速呼叫。当慢速呼叫的百分比等于或大于阈值时，断路器转换为断开并开始短路呼叫。slowCallDurationThreshold |
| ringBufferSizeInClosedState                  | 100                                                      | 设置当断路器处于CLOSED状态下的ring buffer的大小，它存储了最近一段时间请求的成功失败状态。 |
| slidingWindowType                            | COUNT_BASED                                              | 配置用于记录CircuitBreaker关闭时调用结果的滑动窗口的类型。<br/>滑动窗口可以是基于计数的，也可以是基于时间的。<br/>如果滑动窗口为 COUNT_BASED，则记录并汇总最后一次调用。 如果滑动窗口是 TIME_BASED，则记录和聚合最后几秒的调用 |
| slidingWindowSize                            | 100                                                      | 配置用于记录关闭时调用窗口的窗口大小。                       |
| minimumNumberOfCalls                         | 100                                                      | 配置在断路器计算错误率或慢速调用率之前所需的最小调用数（每个滑动窗口周期）。<br/>例如，如果minimumNumberOfCalls为10，则必须至少记录10个呼叫，然后才能计算失败率。<br/>如果仅记录了9个呼叫，则即使有9个呼叫都失败，断路器也不会转换为打开状态。 |
| permittedNumberOfCallsInHalfOpenState        | 10                                                       | 配置半开时允许的呼叫数量                                     |
| ringBufferSizeInHalfOpenState                | 10                                                       | 设置当断路器处于HALF_OPEN状态下的ring buffer的大小,它存储了最近一段时间请求的成功失败状态。 |
| automaticTransitionFromOpenToHalfOpenEnabled | false                                                    | 如果设置为 true，则意味着 CircuitBreaker 将自动从打开状态转换为半打开状态，并且不需要调用来触发转换。创建一个线程来监视 CircuitBreakers 的所有实例，一旦 waitDurationInOpenState 通过，将它们转换为 HALF_OPEN。然而，如果设置为 false，则仅在进行调用时才会转换到 HALF_OPEN，即使在传递了 waitDurationInOpenState 之后也是如此。这里的优点是没有线程监视所有断路器的状态。 |
| writableStackTraceEnabled                    | true                                                     | 配置控制 CallNotPermittedException 的堆栈跟踪中的信息量      |
| allowHealthIndicatorToFail                   | false                                                    | 允许运行状况指标失效                                         |
| eventConsumerBufferSize                      | null                                                     | 事件缓冲区大小                                               |
| registerHealthIndicator                      | null                                                     | 健康监测                                                     |
| recordFailurePredicate                       | throwable -> true<br/>默认情况下，所有异常都记录为失败。 | 一个自定义Predicate，用于评估是否应将异常记录为失败。<br/>如果异常应算作失败，则谓词必须返回 true。如果异常<br/>应算作成功，则谓词必须返回 false，除非异常被 显式忽略。 |
| recordExceptions                             | null                                                     | 记录为失败并因此增加失败率的异常列表。<br/>任何匹配或从列表之一继承的异常都算作失败，除非通过。 如果您指定异常列表，则所有其他异常都算作成功，除非它们被明确忽略 |
| ignoreExceptions                             | null                                                     | 被忽略且既不计为失败也不计为成功的异常列表。                 |
| baseConfig                                   | null                                                     | 默认的实例配置                                               |
| enableExponentialBackoff                     | null                                                     | 是否允许使用指数退避算法进行重试间隔时间的计算               |
| exponentialBackoffMultiplier                 | null                                                     | 指数退避乘数值                                               |
| exponentialMaxWaitDurationInOpenState        | null                                                     | 指数最大间隔值                                               |
| enableRandomizedWait                         | null                                                     | 启用随机延迟策略或不启用重试策略延迟的标志                   |
| randomizedWaitFactor                         | null                                                     | 随机延迟因子值                                               |
|                                              |                                                          |                                                              |

## 3.3 CircuitBreaker源码实现

源码实现在CircuitBreaker.decorateCheckedSupplier()方法，见下图：

![image-1656813648686](https://oss.sparksys.top/halo/image-1656813648686.png)

```java
static <T> CheckedFunction0<T> decorateCheckedSupplier(CircuitBreaker circuitBreaker,
        CheckedFunction0<T> supplier) {
        return () -> {
            // 申请执行函数方法supplier.apply()的许可
            // 具体逻辑在CircuiBreakerStateMachine中的CircuitBreakerState中实现
            circuitBreaker.acquirePermission();
            final long start = circuitBreaker.getCurrentTimestamp();
            try {
                // 执行目标方法
                T result = supplier.apply();
                long duration = circuitBreaker.getCurrentTimestamp() - start;
                //目标方法执行完调用onResult(),check result最终调用onSuccess()
                circuitBreaker.onResult(duration, circuitBreaker.getTimestampUnit(), result);
                return result;
            } catch (Exception exception) {
                // Do not handle java.lang.Error
                long duration = circuitBreaker.getCurrentTimestamp() - start;
                // 如果出现异常就调用onError(),执行onError策略的逻辑
                circuitBreaker.onError(duration, circuitBreaker.getTimestampUnit(), exception);
                throw exception;
            }
        };
    }
```

大体流程如下图

![circuitBreaker原理](https://oss.sparksys.top/halo/circuitBreaker%E5%8E%9F%E7%90%86.jpg)

## 3.4 CircuitBreaker使用示例

1. .引入maven依赖

```xml
<!-- 引入 Resilience4j Starter 相关依赖，并实现对其的自动配置 -->
<dependency>
  <groupId>io.github.resilience4j</groupId>
  <artifactId>resilience4j-spring-boot2</artifactId>
  <version>1.4.0</version>
</dependency>
```

2. 配置文件
   创建 application.yml 配置文件，添加 Resilience4j CircuitBreaker 相关配置项。

```yaml
resilience4j:
  # Resilience4j 的熔断器配置项，对应 CircuitBreakerProperties 属性类
  circuitbreaker:
    instances:
      backendA:
        failure-rate-threshold: 50 # 熔断器关闭状态和半开状态使用的同一个失败率阈值，单位：百分比。默认为 50
        ring-buffer-size-in-closed-state: 5 # 熔断器关闭状态的缓冲区大小，不会限制线程的并发量，在熔断器发生状态转换前所有请求都会调用后端服务。默认为 100
        ring-buffer-size-in-half-open-state: 5 # 熔断器半开状态的缓冲区大小，会限制线程的并发量。例如，缓冲区为 10 则每次只会允许 10 个请求调用后端服务。默认为 10
        wait-duration-in-open-state : 5000 # 熔断器从打开状态转变为半开状态等待的时间，单位：微秒
        automatic-transition-from-open-to-half-open-enabled: true # 如果置为 true，当等待时间结束会自动由打开变为半开；若置为 false，则需要一个请求进入来触发熔断器状态转换。默认为 true
        register-health-indicator: true # 是否注册到健康监测
```

**①** <code>resilience4j.circuitbreaker</code> 是 Resilience4j
的熔断器配置项，对应 [CircuitBreakerProperties](https://gitee.com/mirrors/Resilience4j/blob/master/resilience4j-spring-boot2/src/main/java/io/github/resilience4j/circuitbreaker/autoconfigure/CircuitBreakerProperties.java)
属性类。

**②** 在<code> resilience4j.circuitbreaker.instances</code> 配置项下，可以添加熔断器实例的配置，其中 key 为熔断器实例的名字，value
为熔断器实例的具体配置，对应 [CommonCircuitBreakerConfigurationProperties. InstanceProperties](https://gitee.com/mirrors/Resilience4j/blob/master/resilience4j-framework-common/src/main/java/io/github/resilience4j/common/circuitbreaker/configuration/CommonCircuitBreakerConfigurationProperties.java)
类。

这里，我们创建了一个实例名为 <code>"backendA"</code> 的熔断器。

**③** 在 <code>resilience4j.circuitbreaker.configs</code> 配置项下，可以添加通用配置项，提供给 <code>resilience4j.circuitbreaker.instances</code> 熔断器使用。示例如下图：

![13](https://oss.sparksys.top/halo/13.png)

3. 创建 DemoController 类，提供调用用户服务的 HTTP API 接口。代码如下：

```java
@RestController
@RequestMapping("/demo")
public class DemoController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/get_user")
    @CircuitBreaker(name = "backendA", fallbackMethod = "getUserFallback")
    public String getUser(@RequestParam("id") Integer id) {
        logger.info("[getUser][准备调用 user-service 获取用户({})详情]", id);
        return restTemplate.getForEntity("http://127.0.0.1:18080/user/get?id=" + id, String.class).getBody();
    }

    public String getUserFallback(Integer id, Throwable throwable) {
        logger.info("[getUserFallback][id({}) exception({})]", id, throwable.getClass().getSimpleName());
        return "mock:User:" + id;
    }

}
```

**①** 在 <code>#getUser(Integer id)</code> 方法中，我们使用 RestTemplate 调用用户服务提供的 <code>/user/get</code> 接口，获取用户详情。

**②** 在 <code>#getUser(Integer id)</code> 方法上，添加了 Resilience4j 提供的 @CircuitBreaker 注解：

- 通过 <code>name</code> 属性，设置对应的 CircuitBreaker 熔断器实例名为 <code>"backendA"</code>，就是我们在「2. 配置文件」中所添加的。
- 通过 <code>fallbackMethod</code> 属性，设置执行发生 Exception 异常时，执行对应的 <code>#getUserFallback(Integer id, Throwable throwable)</code>
  方法。注意，fallbackMethod 方法的参数要和**原始方法一致**，最后一个为 **Throwable** 异常。

通过不同的 Throwable 异常，我们可以进行不同的 fallback 降级处理。极端情况下，Resilience4j 熔断器打开时，不会执行 **#getUser(Integer id)** 方法，而是直接抛出 **CallNotPermittedException**
异常，然后也是进入 fallback 降级处理。

## 3.5 CircuitBreaker原理

上面可知，加入 <code>@CircuitBreaker(name = "backendA", fallbackMethod = "getUserFallback")</code> 即可轻松实现熔断，接下来我们看一下源码实现

- 核心就是使用了spring的Aspect切面，

![image-1656640092187](https://oss.sparksys.top/halo/image-1656640092187.png)

通过 <code>@Pointcut(value = "@within(circuitBreaker) || @annotation(circuitBreaker)", argNames = "circuitBreaker")</code> 来动态代理执行

![image-1656640275748](https://oss.sparksys.top/halo/image-1656640275748.png)

![image-1656641488231](https://oss.sparksys.top/halo/image-1656641488231.png)

# 4 Bulkhead 舱壁

## 4.1 概念理解

> Bulkhead 指的是船舶中的舱壁，它将船体分隔为多个船舱，在船部分受损时可避免沉船。
> 在 Resilience4j 中，提供了基于 **Semaphore** 信号量和 **ThreadPool** 线程池两种 Bulkhead 实现，**隔离不同种类的调用**，并提供流控的能力，从而避免某类调用异常时而占用所有资源，导致影响整个系统。

## 4.2 信号量使用示例

1. 配置文件

```java
resilience4j:
  # Resilience4j 的信号量 Bulkhead 配置项，对应 BulkheadConfigurationProperties 属性类
  bulkhead:
    instances:
      backendC:
        max-concurrent-calls: 1 # 并发调用数。默认为 25
        max-wait-duration: 5s # 并发调用到达上限时，阻塞等待的时长，单位：微秒。默认为 0
```

**①** <code>resilience4j.bulkhead</code> 是 Resilience4j 的信号量 Bulkhead
配置项，对应 [BulkheadProperties](https://gitee.com/mirrors/Resilience4j/blob/master/resilience4j-spring-boot2/src/main/java/io/github/resilience4j/bulkhead/autoconfigure/BulkheadProperties.java)
属性类。

**②** 在 <code>resilience4j.bulkhead.instances</code> 配置项下，可以添加 Bulkhead 实例的配置，其中 key 为 Bulkhead 实例的名字，value 为 Bulkhead
实例的具体配置，对应 [CommonBulkheadConfigurationProperties.InstanceProperties](https://gitee.com/mirrors/Resilience4j/blob/master/resilience4j-framework-common/src/main/java/io/github/resilience4j/common/bulkhead/configuration/CommonBulkheadConfigurationProperties.java)
类。
这里，我们创建了一个实例名为 "**backendC**" 的 Bulkhead，有一点要注意，在请求被流控时，并不是直接失败抛出异常，而是阻塞等待最大 <code>max-wait-duration</code> 微秒，看看是否能够请求通过。

**③** 在 <code>resilience4j.bulkhead.configs</code> 配置项下，可以添加通用配置项，提供给 <code>resilience4j.bulkhead.instances Bulkhead</code> 使用。示例如下图：
![image-1656642584556](https://oss.sparksys.top/halo/image-1656642584556.png)

2. 创建 BulkheadDemoController类

```java
@RestController
@RequestMapping("/bulkhead-demo")
public class BulkheadDemoController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping("/get_user")
    @Bulkhead(name = "backendC", fallbackMethod = "getUserFallback", type = Bulkhead.Type.SEMAPHORE)
    public String getUser(@RequestParam("id") Integer id) throws InterruptedException {
        logger.info("[getUser][id({})]", id);
        Thread.sleep(10 * 1000L); // sleep 10 秒
        return "User:" + id;
    }

    public String getUserFallback(Integer id, Throwable throwable) {
        logger.info("[getUserFallback][id({}) exception({})]", id, throwable.getClass().getSimpleName());
        return "mock:User:" + id;
    }

}
```

**①** 在 <code>#getUser(Integer id)</code> 方法中，我们直接返回 <code>"User:{id}"</code>，不进行任何逻辑。不过，这里为了模拟调用执行一定时长，通过 sleep 10 秒来实现。

**②** 在 <code>#getUser(Integer id)</code> 方法上，添加了 Resilience4j 提供的 <code>@Bulkhead</code> 注解：

- 通过 <code>name</code> 属性，设置对应的 Bulkhead 实例名为 "backendC"，就是我们在「4.1 配置文件」中所添加的。
- 通过 <code>type</code> 属性，设置 Bulkhead 类型为**信号量**的方式。
- 通过 <code>fallbackMethod</code> 属性，设置执行发生 Exception 异常时，执行对应的 <code>#getUserFallback(Integer id, Throwable throwable)</code> 方法。注意，<code>
  fallbackMethod</code> 方法的参数要和**原始**方法一致，最后一个为 **Throwable** 异常。

在请求被流控时，Resilience4j 不会执行 <code>#getUser(Integer id)</code> 方法，而是直接抛出 **BulkheadFullException** 异常，然后就进入 fallback 降级处理。

## 4.3 线程池Bulkhead示例

1. 配置文件

> 修改 <code>application.yml</code> 配置文件，添加 Resilience4j 信号量类型的 Bulkhead 相关配置项。

```yaml
resilience4j:
  # Resilience4j 的线程池 Bulkhead 配置项，对应 ThreadPoolBulkheadProperties 属性类
  thread-pool-bulkhead:
    instances:
      backendD:
        max-thread-pool-size: 1 # 线程池的最大大小。默认为 Runtime.getRuntime().availableProcessors()
        core-thread-pool-size: 1 # 线程池的核心大小。默认为 Runtime.getRuntime().availableProcessors() - 1
        queue-capacity: 100 # 线程池的队列大小。默认为 100
        keep-alive-duration: 100s # 超过核心大小的线程，空闲存活时间。默认为 20 毫秒
```

**①** <code>resilience4j.thread-pool-bulkhead</code> 是 Resilience4j 的线程池 Bulkhead
配置项，对应 [ThreadPoolBulkheadProperties](https://gitee.com/mirrors/Resilience4j/blob/master/resilience4j-spring-boot2/src/main/java/io/github/resilience4j/bulkhead/autoconfigure/ThreadPoolBulkheadProperties.java)
属性类。

**② 在 <code>resilience4j.thread-pool-bulkhead.instances</code> 配置项下，可以添加 Bulkhead 实例的配置，其中 key 为 Bulkhead 实例的名字，value 为 Bulkhead
实例的具体配置，对应 [CommonBulkheadConfigurationProperties.InstanceProperties](https://gitee.com/mirrors/Resilience4j/blob/master/resilience4j-framework-common/src/main/java/io/github/resilience4j/common/bulkhead/configuration/CommonBulkheadConfigurationProperties.java)
类。

这里，我们创建了一个实例名为 "backendD" 的 Bulkhead。

**③** 在 <code>resilience4j.thread-pool-bulkhead.configs</code> 配置项下，可以添加通用配置项，提供给 <code>resilience4j.thread-pool-bulkhead.instances
Bulkhead</code> 使用。示例如下图：
![33](https://oss.sparksys.top/halo/33.png)

2. 创建 ThreadPoolBulkheadDemoController 类

```java
@RestController
@RequestMapping("/thread-pool-bulkhead-demo")
public class ThreadPoolBulkheadDemoController {

    @Autowired
    private ThreadPoolBulkheadService threadPoolBulkheadService;

    @GetMapping("/get_user")
    public String getUser(@RequestParam("id") Integer id) throws ExecutionException, InterruptedException {
        threadPoolBulkheadService.getUser0(id);
        return threadPoolBulkheadService.getUser0(id).get();
    }

    @Service
    public static class ThreadPoolBulkheadService {

        private final Logger logger = LoggerFactory.getLogger(ThreadPoolBulkheadService.class);

        @Bulkhead(name = "backendD", fallbackMethod = "getUserFallback", type = Bulkhead.Type.THREADPOOL)
        public CompletableFuture<String> getUser0(Integer id) throws InterruptedException {
            logger.info("[getUser][id({})]", id);
            Thread.sleep(10 * 1000L); // sleep 10 秒
            return CompletableFuture.completedFuture("User:" + id);
        }

        public CompletableFuture<String> getUserFallback(Integer id, Throwable throwable) {
            logger.info("[getUserFallback][id({}) exception({})]", id, throwable.getClass().getSimpleName());
            return CompletableFuture.completedFuture("mock:User:" + id);
        }

    }
}
```

> 友情提示：这里创建了 <code>ThreadPoolBulkheadService</code> 的原因是，这里我们使用 Resilience4j 是基于注解 + AOP的方式，如果直接 this. 方式来调用方法，实际没有走代理，导致 Resilience4j 无法使用
> AOP。

**①** 在 <code>#getUser(Integer id)</code> 方法中，我们调用了 2 次 ThreadPoolBulkheadService 的 <code>#getUser0(Integer id)</code> 方法，测试在线程池 Bulkhead
下，且线程池大小为 1 时，被流控成“串行”执行。

**②** 在 <code>#getUser0(Integer id)</code> 方法上，添加了 Resilience4j 提供的 <code>@Bulkhead</code> 注解：

通过 <code>name</code> 属性，设置对应的 Bulkhead 实例名为 "<code>backendC</code>"，就是我们在「1. 配置文件」中所添加的。
通过 <code>type</code> 属性，设置 Bulkhead 类型为**线程池**的方式。
通过 <code>fallbackMethod</code> 属性，设置执行发生 Exception 异常时，执行对应的 <code>#getUserFallback(Integer id, Throwable throwable)</code>
方法。注意，fallbackMethod 方法的参数要和**原始**方法一致，最后一个为 **Throwable** 异常。

注意！！！方法的返回类型必须是 CompletableFuture 类型，包括 fallback 方法，否则会报异常，毕竟要提交线程池中执行。
在请求被流控时，Resilience4j 不会执行 <code>#getUser0(Integer id)</code> 方法，而是直接抛出 <code>BulkheadFullException</code> 异常，然后就进入 fallback 降级处理。

## 4.4 Bulkhead源码实现

上面可知，加入 <code>@Bulkhead(name = "backendC", fallbackMethod = "getUserFallback", type = Bulkhead.Type.SEMAPHORE)
</code> 即可轻松实现熔断，接下来我们看一下源码实现

![image-1656643313409](https://oss.sparksys.top/halo/image-1656643313409.png)

![image-1656643384484](https://oss.sparksys.top/halo/image-1656643384484.png)

![image-1656643474269](https://oss.sparksys.top/halo/image-1656643474269.png)

# 5 resilience4j 重试

> 微服务系统中，会遇到**在线发布**，一般的发布更新策略是：**启动一个新的，启动成功之后，关闭一个旧的，直到所有的旧的都被关闭**。Spring Boot 具有**优雅关闭**的功能，可以保证请求处理完再关闭，同时会拒绝新的请求。对于这些拒绝的请求，为了保证**用户体验**
> 不受影响，是需要**重试**的。Resilience4j 提供了 **Retry** 组件，在执行失败时，进行重试的行为。

## 5.1 使用示例

1. 配置文件

> 修改 application.yml 配置文件，添加 Resilience4j Retry 相关配置项。

```yaml
resilience4j:
  # Resilience4j 的重试 Retry 配置项，对应 RetryProperties 属性类
  retry:
    instances:
      backendE:
        max-retry-Attempts: 3 # 最大重试次数。默认为 3
        wait-duration: 5s # 下次重试的间隔，单位：微秒。默认为 500 毫秒
        retry-exceptions: # 需要重试的异常列表。默认为空
        ingore-exceptions: # 需要忽略的异常列表。默认为空
```

**①** <code>resilience4j.retry</code> 是 Resilience4j 的 Retry
配置项，对应 [RetryProperties](https://gitee.com/mirrors/Resilience4j/blob/master/resilience4j-spring-boot2/src/main/java/io/github/resilience4j/retry/autoconfigure/RetryProperties.java)
属性类。

**②** 在 <code>resilience4j.retry.instances</code> 配置项下，可以添加 Retry 实例的配置，其中 key 为 Retry **实例的名字**，value 为 Retry
实例的具体配置，对应 [CommonBulkheadConfigurationProperties.InstanceProperties](https://gitee.com/mirrors/Resilience4j/blob/master/resilience4j-framework-common/src/main/java/io/github/resilience4j/common/bulkhead/configuration/CommonBulkheadConfigurationProperties.java)
类。

这里，我们创建了一个实例名为 "backendE" 的 Retry。
**③** 在 <code>resilience4j.retry.configs</code> 配置项下，可以添加通用配置项，提供给 <code>resilience4j.retry.instances</code> Retry 使用。示例如下图：
![42](https://oss.sparksys.top/halo/42.png)

2. RetryDemoController

> 创建 <code>RetryDemoController</code> 类，提供调用用户服务的 HTTP API 接口。代码如下：

```java

@RestController
@RequestMapping("/retry-demo")
public class RetryDemoController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/get_user")
    @Retry(name = "backendE", fallbackMethod = "getUserFallback")
    public String getUser(@RequestParam("id") Integer id) {
        logger.info("[getUser][准备调用 user-service 获取用户({})详情]", id);
        return restTemplate.getForEntity("http://127.0.0.1:18080/user/get?id=" + id, String.class).getBody();
    }

    public String getUserFallback(Integer id, Throwable throwable) {
        logger.info("[getUserFallback][id({}) exception({})]", id, throwable.getClass().getSimpleName());
        return "mock:User:" + id;
    }

}
```

**①** 在 <code>#getUser(Integer id)</code> 方法中，我们使用 RestTemplate 调用用户服务提供的 <code>/user/get</code> 接口，获取用户详情。

**②** 在 <code>#getUser(Integer id)</code> 方法上，添加了 Resilience4j 提供的 <code>@Retry</code> 注解：

- 通过 <code>name</code> 属性，设置对应的 Retry 实例名为 "<code>backendE</code>"，就是我们在「1 配置文件」中所添加的。
- 通过 <code>fallbackMethod</code> 属性，设置执行发生 Exception 异常时，执行对应的 <code>#getUserFallback(Integer id, Throwable throwable) </code>方法。注意，<code>
  fallbackMethod </code>方法的参数要和原始方法一致，最后一个为 **Throwable** 异常。
  在多次重试失败到达上限时，Resilience4j 会抛出 MaxRetriesExceeded 异常，然后就进入 fallback 降级处理。

## 5.2 retry源码实现

上面可知，加入 <code>@Retry(name = "backendE", fallbackMethod = "getUserFallback")</code>
即可轻松实现重试，接下来我们看一下源码实现

![image-1656645941254](https://oss.sparksys.top/halo/image-1656645941254.png)

![image-1656645977294](https://oss.sparksys.top/halo/image-1656645977294.png)

# 6 Resilience4j 熔断、舱壁、重试最佳实践

1. 添加Resilience4j 的注解

> 我们在相同方法上，添加Resilience4j 的注解，从而组合使用熔断、限流、舱壁、重试、重试的功能

```java
@CircuitBreaker(name = BACKEND, fallbackMethod = "fallback")
@RateLimiter(name = BACKEND)
@Bulkhead(name = BACKEND)
@Retry(name = BACKEND, fallbackMethod = "fallback")
@TimeLimiter(name = BACKEND)
public String method(String param1) {
    throws new Exception("xxxx");
}

private String fallback(String param1, IllegalArgumentException e) {
    return "test:IllegalArgumentException";
}

private String fallback(String param1, RuntimeException e) {
    return "test:RuntimeException";
}
```

此时，我们就要注意它们的执行顺序是 ：

```text
Retry > Bulkhead > RateLimiter > TimeLimiter > Bulkhead
```

|注解|切面|顺序|
|-------|-------|-------|
|@Retry|[RetryAspect](https://gitee.com/mirrors/Resilience4j/blob/master/resilience4j-spring/src/main/java/io/github/resilience4j/retry/configure/RetryAspect.java)|Ordered.LOWEST_PRECEDENCE - 4|
|@CircuitBreaker|[CircuitBreakerAspect](https://gitee.com/mirrors/Resilience4j/blob/master/resilience4j-spring/src/main/java/io/github/resilience4j/circuitbreaker/configure/CircuitBreakerAspect.java)|Ordered.LOWEST_PRECEDENCE - 3|
|@RateLimiter|[RateLimiterAspect](https://gitee.com/mirrors/Resilience4j/blob/master/resilience4j-spring/src/main/java/io/github/resilience4j/ratelimiter/configure/RateLimiterAspect.java)|Ordered.LOWEST_PRECEDENCE - 2|
|@TimeLimiter|[TimeLimiterAspect](https://gitee.com/mirrors/Resilience4j/blob/master/resilience4j-spring/src/main/java/io/github/resilience4j/timelimiter/configure/TimeLimiterAspect.java)|Ordered.LOWEST_PRECEDENCE - 1|
|@Bulkhead|    [BulkheadAspect](https://gitee.com/mirrors/Resilience4j/blob/master/resilience4j-spring/src/main/java/io/github/resilience4j/bulkhead/configure/BulkheadAspect.java)|Ordered.LOWEST_PRECEDENCE|

![image-1656648883702](https://oss.sparksys.top/halo/image-1656648883702.png)

# 7 彩蛋

至此，我们已经完成了 Resilience4j 的入门。总的来说，因为Resilience4j 几个组件的拆分非常干净，所以理解起来还是蛮轻松的。

后续，可以自己在看看[《Resilience4j 官方文档》](https://resilience4j.readme.io/docs)，进行下查漏补缺。

另外，熔断组件的选型上的思考，可以参考以下图进行选择：
![5248b770c05cc1141b2d4d9ccfe3df72](https://oss.sparksys.top/halo/5248b770c05cc1141b2d4d9ccfe3df72.jpeg)

下一章我们即将讲([《Spring Cloud进阶之路之resilience4j feign实现重试、断路器以及线程隔离》](11)，敬请期待！

# 公众号

学习不走弯路，关注公众号「凛冬王昭君」

![wechat-sparkzxl.jpg](https://oss.sparksys.top/sparkzxl-framework/wechat-sparkzxl.jpg)