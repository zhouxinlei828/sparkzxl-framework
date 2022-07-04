# 1 circuitbreaker简介

Spring Cloud 断路器提供了跨不同断路器实现的抽象。它提供了在您的应用程序中使用的一致 API，让您开发人员可以选择最适合您的应用程序需求的断路器实现。

# 2 支持的断路器类型

- Resilience4J
- Spring Retry

# 3 核心源码实现

spring-cloud-commons提供了circuitbreaker的核心定义接口，我们接下来来看一下

## 3.1 CircuitBreaker

```java
/**
 * Spring Cloud circuit breaker.
 *
 * @author Ryan Baxter
 */
public interface CircuitBreaker {

	default <T> T run(Supplier<T> toRun) {
		return run(toRun, throwable -> {
			throw new NoFallbackAvailableException("No fallback available.", throwable);
		});
	}

	<T> T run(Supplier<T> toRun, Function<Throwable, T> fallback);

}
```

定义了核心断路器的接口，提供了run方法的默认实现和核心接口扩展实现
<code>default <T> T run(Supplier<T> toRun)</code>方法，如果出现异常，抛出<code>NoFallbackAvailableException</code>

## 3.2 CircuitBreakerFactory(断路器工厂API)

  ```java
  /**
 * Creates circuit breakers based on the underlying implementation.
 *
 * @author Ryan Baxter
 * @author Andrii Bohutskyi
 */
public abstract class CircuitBreakerFactory<CONF, CONFB extends ConfigBuilder<CONF>>
		extends AbstractCircuitBreakerFactory<CONF, CONFB> {

	public abstract CircuitBreaker create(String id);

	public CircuitBreaker create(String id, String groupName) {
		return create(id);
	}

}
  ```

定义了断路器工厂，构建断路器对象

- <code>id</code>断路器的操作或方法名称
- <code>groupName</code> 服务分组名称

## 3.3 Resilience4j实现断路器

![image-1656897792384](https://oss.sparksys.top/halo/image-1656897792384.png)

1. 核心方法<code>ReactiveResilience4JCircuitBreaker</code>

  ```java
  @Override
	public <T> T run(Supplier<T> toRun, Function<Throwable, T> fallback) {
		final io.vavr.collection.Map<String, String> tags = io.vavr.collection.HashMap.of(CIRCUIT_BREAKER_GROUP_TAG,
				this.groupName);
		// 构建限速器
		TimeLimiter timeLimiter = this.timeLimiterRegistry.find(this.id)
				.orElseGet(() -> this.timeLimiterRegistry.find(this.groupName)
						.orElseGet(() -> this.timeLimiterRegistry.timeLimiter(this.id, this.timeLimiterConfig, tags)));
		// 构建熔断器
		io.github.resilience4j.circuitbreaker.CircuitBreaker defaultCircuitBreaker = registry.circuitBreaker(this.id,
				this.circuitBreakerConfig, tags);
		circuitBreakerCustomizer.ifPresent(customizer -> customizer.customize(defaultCircuitBreaker));
		// 舱壁提供者不为空，执行业务逻辑
		if (bulkheadProvider != null) {
			return bulkheadProvider.run(this.groupName, toRun, fallback, defaultCircuitBreaker, timeLimiter, tags);
		}
		else {
			if (executorService != null) {
				Supplier<Future<T>> futureSupplier = () -> executorService.submit(toRun::get);
				Callable restrictedCall = TimeLimiter.decorateFutureSupplier(timeLimiter, futureSupplier);
				Callable<T> callable = io.github.resilience4j.circuitbreaker.CircuitBreaker
						.decorateCallable(defaultCircuitBreaker, restrictedCall);
				return Try.of(callable::call).recover(fallback).get();
			}
			else {
				Supplier<T> decorator = io.github.resilience4j.circuitbreaker.CircuitBreaker
						.decorateSupplier(defaultCircuitBreaker, toRun);
				return Try.of(decorator::get).recover(fallback).get();
			}

		}
	}
  ```

2. 再看一下<code>Resilience4jBulkheadProvider.run()</code>

  ```java

	public <T> T run(String id, Supplier<T> toRun, Function<Throwable, T> fallback, CircuitBreaker circuitBreaker,
			TimeLimiter timeLimiter, io.vavr.collection.Map<String, String> tags) {
		Supplier<CompletionStage<T>> bulkheadCall = decorateBulkhead(id, tags, toRun);
		final Callable<T> timeLimiterCall = decorateTimeLimiter(bulkheadCall, timeLimiter);
		final Callable<T> circuitBreakerCall = circuitBreaker.decorateCallable(timeLimiterCall);
		return Try.of(circuitBreakerCall::call).recover(fallback).get();
	}

	private <T> Supplier<CompletionStage<T>> decorateBulkhead(final String id,
			final io.vavr.collection.Map<String, String> tags, final Supplier<T> supplier) {
		Resilience4jBulkheadConfigurationBuilder.BulkheadConfiguration configuration = configurations
				.computeIfAbsent(id, defaultConfiguration);

		if (semaphoreDefaultBulkhead
				|| (bulkheadRegistry.find(id).isPresent() && !threadPoolBulkheadRegistry.find(id).isPresent())) {
			Bulkhead bulkhead = bulkheadRegistry.bulkhead(id, configuration.getBulkheadConfig(), tags);
			CompletableFuture<T> asyncCall = CompletableFuture.supplyAsync(supplier);
			return Bulkhead.decorateCompletionStage(bulkhead, () -> asyncCall);
		}
		else {
			ThreadPoolBulkhead threadPoolBulkhead = threadPoolBulkheadRegistry.bulkhead(id,
					configuration.getThreadPoolBulkheadConfig(), tags);
			return threadPoolBulkhead.decorateSupplier(supplier);
		}
	}

	private <T> Callable<T> decorateTimeLimiter(final Supplier<CompletionStage<T>> supplier, TimeLimiter timeLimiter) {
		final Supplier<Future<T>> futureSupplier = () -> supplier.get().toCompletableFuture();
		return timeLimiter.decorateFutureSupplier(futureSupplier);
	}
  ```

通过源码得知，<code>run</code>方法中，执行顺序为**Bulkhead->timeLimiter->circuitBreaker**，如果发生异常，执行降级。

## 3.4 Spring Retry 实现断路器

Spring Retry 实现比较简单，我这边不做过多详解
，提供核心源码执行逻辑给各位看官老爷。

```java
  @Override
	public <T> T run(Supplier<T> toRun, Function<Throwable, T> fallback) {

		retryTemplate.setBackOffPolicy(config.getBackOffPolicy());
		retryTemplate.setRetryPolicy(config.getRetryPolicy());

		if (retryTemplateCustomizer != null) {
			retryTemplateCustomizer.customize(retryTemplate);
		}

		return retryTemplate.execute(context -> toRun.get(), context -> fallback.apply(context.getLastThrowable()),
				new DefaultRetryState(id, config.isForceRefreshState(), config.getStateClassifier()));
	}
  ```

# 4 Spring Cloud Gateway中Circuitbreaker的实现

Spring Cloud Gateway我这边不做过多介绍，重点讲解一下Gateway中Circuitbreaker的实现。Spring Cloud Gateway中Circuitbreaker的实现是通过<code>
SpringCloudCircuitBreakerFilterFactory</code>以及<code>SpringCloudCircuitBreakerResilience4JFilterFactory</code>实现的，我们先看一下<code>
SpringCloudCircuitBreakerFilterFactory</code>的实现。

## 4.1 SpringCloudCircuitBreakerFilterFactory源码实现

 ```java
  public abstract class SpringCloudCircuitBreakerFilterFactory
        extends AbstractGatewayFilterFactory<SpringCloudCircuitBreakerFilterFactory.Config> {

    /** CircuitBreaker component name. */
    public static final String NAME = "CircuitBreaker";

    private final ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory;

    private ReactiveCircuitBreaker cb;

    private final ObjectProvider<DispatcherHandler> dispatcherHandlerProvider;

    // do not use this dispatcherHandler directly, use getDispatcherHandler() instead.
    private volatile DispatcherHandler dispatcherHandler;

    public SpringCloudCircuitBreakerFilterFactory(ReactiveCircuitBreakerFactory reactiveCircuitBreakerFactory,
                                                  ObjectProvider<DispatcherHandler> dispatcherHandlerProvider) {
        super(Config.class);
        this.reactiveCircuitBreakerFactory = reactiveCircuitBreakerFactory;
        this.dispatcherHandlerProvider = dispatcherHandlerProvider;
    }

    private DispatcherHandler getDispatcherHandler() {
        if (dispatcherHandler == null) {
            dispatcherHandler = dispatcherHandlerProvider.getIfAvailable();
        }

        return dispatcherHandler;
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return singletonList(NAME_KEY);
    }

    @Override
    public GatewayFilter apply(Config config) {
        ReactiveCircuitBreaker cb = reactiveCircuitBreakerFactory.create(config.getId());
        Set<HttpStatus> statuses = config.getStatusCodes().stream().map(HttpStatusHolder::parse)
                .filter(statusHolder -> statusHolder.getHttpStatus() != null).map(HttpStatusHolder::getHttpStatus)
                .collect(Collectors.toSet());

        return new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                return cb.run(chain.filter(exchange).doOnSuccess(v -> {
                    if (statuses.contains(exchange.getResponse().getStatusCode())) {
                        HttpStatusCode status = exchange.getResponse().getStatusCode();
                        throw new CircuitBreakerStatusCodeException(status);
                    }
                }), t -> {
                    if (config.getFallbackUri() == null) {
                        return Mono.error(t);
                    }

                    exchange.getResponse().setStatusCode(null);
                    reset(exchange);

                    // TODO: copied from RouteToRequestUrlFilter
                    URI uri = exchange.getRequest().getURI();
                    // TODO: assume always?
                    boolean encoded = containsEncodedParts(uri);
                    URI requestUrl = UriComponentsBuilder.fromUri(uri).host(null).port(null)
                            .uri(config.getFallbackUri()).scheme(null).build(encoded).toUri();
                    exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUrl);
                    addExceptionDetails(t, exchange);

                    // Reset the exchange
                    reset(exchange);

                    ServerHttpRequest request = exchange.getRequest().mutate().uri(requestUrl).build();
                    return getDispatcherHandler().handle(exchange.mutate().request(request).build());
                }).onErrorResume(t -> handleErrorWithoutFallback(t, config.isResumeWithoutError()));
            }

            @Override
            public String toString() {
                return filterToStringCreator(SpringCloudCircuitBreakerFilterFactory.this)
                        .append("name", config.getName()).append("fallback", config.fallbackUri).toString();
            }
        };
    }

    protected abstract Mono<Void> handleErrorWithoutFallback(Throwable t, boolean resumeWithoutError);

    private void addExceptionDetails(Throwable t, ServerWebExchange exchange) {
        ofNullable(t).ifPresent(
                exception -> exchange.getAttributes().put(CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR, exception));
    }

    @Override
    public String name() {
        return NAME;
    }

    public static class Config implements HasRouteId {

        private String name;

        private URI fallbackUri;

        private String routeId;

        private Set<String> statusCodes = new HashSet<>();

        private boolean resumeWithoutError = false;

        @Override
        public void setRouteId(String routeId) {
            this.routeId = routeId;
        }

        public String getRouteId() {
            return routeId;
        }

        public URI getFallbackUri() {
            return fallbackUri;
        }

        public Config setFallbackUri(URI fallbackUri) {
            this.fallbackUri = fallbackUri;
            return this;
        }

        public Config setFallbackUri(String fallbackUri) {
            return setFallbackUri(URI.create(fallbackUri));
        }

        public String getName() {
            return name;
        }

        public Config setName(String name) {
            this.name = name;
            return this;
        }

        public String getId() {
            if (!StringUtils.hasText(name) && StringUtils.hasText(routeId)) {
                return routeId;
            }
            return name;
        }

        public Set<String> getStatusCodes() {
            return statusCodes;
        }

        public Config setStatusCodes(Set<String> statusCodes) {
            this.statusCodes = statusCodes;
            return this;
        }

        public Config addStatusCode(String statusCode) {
            this.statusCodes.add(statusCode);
            return this;
        }

        public boolean isResumeWithoutError() {
            return resumeWithoutError;
        }

        public void setResumeWithoutError(boolean resumeWithoutError) {
            this.resumeWithoutError = resumeWithoutError;
        }

    }

    public class CircuitBreakerStatusCodeException extends HttpStatusCodeException {

        public CircuitBreakerStatusCodeException(HttpStatusCode statusCode) {
            super(statusCode);
        }

    }
  ```

重点在<code>SpringCloudCircuitBreakerFilterFactory.apply()</code>方法中

- 创建断路器<code>ReactiveCircuitBreaker</code>
- 读取配置属性指定的响应状态码
- 创建GatewayFilter，ReactiveCircuitBreaker.run(执行业务逻辑),如果出现异常，执行重定向配置中给的降级URL进行降级。

## 4.2 gateway启用circuitBreaker

  ```yaml
  spring:
  cloud:
    gateway:
      routes:
        - id: demo
          uri: lb://demo-server
          predicates:
            - Path=/demo/**
          filters:
            - StripPrefix=1
            - name: CircuitBreaker
              args:
                name: backendA
                fallbackUri: forward:/fallback
  ```

# 5. feign Circuitbreaker的实现

## 5.1 FeignCircuitBreaker

 ```java
  public final class FeignCircuitBreaker {

	private FeignCircuitBreaker() {
		throw new IllegalStateException("Don't instantiate a utility class");
	}

	/**
	 * @return builder for Feign CircuitBreaker integration
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder for Feign CircuitBreaker integration.
	 */
	public static final class Builder extends Feign.Builder {

		private CircuitBreakerFactory circuitBreakerFactory;

		private String feignClientName;

		private boolean circuitBreakerGroupEnabled;

		private CircuitBreakerNameResolver circuitBreakerNameResolver;

		Builder circuitBreakerFactory(CircuitBreakerFactory circuitBreakerFactory) {
			this.circuitBreakerFactory = circuitBreakerFactory;
			return this;
		}

		Builder feignClientName(String feignClientName) {
			this.feignClientName = feignClientName;
			return this;
		}

		Builder circuitBreakerGroupEnabled(boolean circuitBreakerGroupEnabled) {
			this.circuitBreakerGroupEnabled = circuitBreakerGroupEnabled;
			return this;
		}

		Builder circuitBreakerNameResolver(CircuitBreakerNameResolver circuitBreakerNameResolver) {
			this.circuitBreakerNameResolver = circuitBreakerNameResolver;
			return this;
		}

		public <T> T target(Target<T> target, T fallback) {
			return build(fallback != null ? new FallbackFactory.Default<T>(fallback) : null).newInstance(target);
		}

		public <T> T target(Target<T> target, FallbackFactory<? extends T> fallbackFactory) {
			return build(fallbackFactory).newInstance(target);
		}

		@Override
		public <T> T target(Target<T> target) {
			return build(null).newInstance(target);
		}

		public Feign build(final FallbackFactory<?> nullableFallbackFactory) {
			super.invocationHandlerFactory((target, dispatch) -> new FeignCircuitBreakerInvocationHandler(
					circuitBreakerFactory, feignClientName, target, dispatch, nullableFallbackFactory,
					circuitBreakerGroupEnabled, circuitBreakerNameResolver));
			return super.build();
		}

	}

}
  ```

<code>FeignCircuitBreaker</code>class类的build方法，使用了<code>FeignCircuitBreakerInvocationHandler</code>动态代理类，接下来我们看一下<code>
FeignCircuitBreakerInvocationHandler</code>动态代理类做了哪些事情

## 5.2 FeignCircuitBreakerInvocationHandler

```java
class FeignCircuitBreakerInvocationHandler implements InvocationHandler {

	private final CircuitBreakerFactory factory;

	private final String feignClientName;

	private final Target<?> target;

	private final Map<Method, InvocationHandlerFactory.MethodHandler> dispatch;

	private final FallbackFactory<?> nullableFallbackFactory;

	private final Map<Method, Method> fallbackMethodMap;

	private final boolean circuitBreakerGroupEnabled;

	private final CircuitBreakerNameResolver circuitBreakerNameResolver;

	FeignCircuitBreakerInvocationHandler(CircuitBreakerFactory factory, String feignClientName, Target<?> target,
			Map<Method, InvocationHandlerFactory.MethodHandler> dispatch, FallbackFactory<?> nullableFallbackFactory,
			boolean circuitBreakerGroupEnabled, CircuitBreakerNameResolver circuitBreakerNameResolver) {
		this.factory = factory;
		this.feignClientName = feignClientName;
		this.target = checkNotNull(target, "target");
		this.dispatch = checkNotNull(dispatch, "dispatch");
		this.fallbackMethodMap = toFallbackMethod(dispatch);
		this.nullableFallbackFactory = nullableFallbackFactory;
		this.circuitBreakerGroupEnabled = circuitBreakerGroupEnabled;
		this.circuitBreakerNameResolver = circuitBreakerNameResolver;
	}

	@Override
	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
		// early exit if the invoked method is from java.lang.Object
		// code is the same as ReflectiveFeign.FeignInvocationHandler
		if ("equals".equals(method.getName())) {
			try {
				Object otherHandler = args.length > 0 && args[0] != null ? Proxy.getInvocationHandler(args[0]) : null;
				return equals(otherHandler);
			}
			catch (IllegalArgumentException e) {
				return false;
			}
		}
		else if ("hashCode".equals(method.getName())) {
			return hashCode();
		}
		else if ("toString".equals(method.getName())) {
			return toString();
		}

		String circuitName = circuitBreakerNameResolver.resolveCircuitBreakerName(feignClientName, target, method);
		CircuitBreaker circuitBreaker = circuitBreakerGroupEnabled ? factory.create(circuitName, feignClientName)
				: factory.create(circuitName);
		Supplier<Object> supplier = asSupplier(method, args);
		if (this.nullableFallbackFactory != null) {
			Function<Throwable, Object> fallbackFunction = throwable -> {
				Object fallback = this.nullableFallbackFactory.create(throwable);
				try {
					return this.fallbackMethodMap.get(method).invoke(fallback, args);
				}
				catch (Exception exception) {
					unwrapAndRethrow(exception);
				}
				return null;
			};
			return circuitBreaker.run(supplier, fallbackFunction);
		}
		return circuitBreaker.run(supplier);
	}

	private void unwrapAndRethrow(Exception exception) {
		if (exception instanceof InvocationTargetException || exception instanceof NoFallbackAvailableException) {
			Throwable underlyingException = exception.getCause();
			if (underlyingException instanceof RuntimeException) {
				throw (RuntimeException) underlyingException;
			}
			if (underlyingException != null) {
				throw new IllegalStateException(underlyingException);
			}
			throw new IllegalStateException(exception);
		}
	}

	private Supplier<Object> asSupplier(final Method method, final Object[] args) {
		final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		return () -> {
			try {
				RequestContextHolder.setRequestAttributes(requestAttributes);
				return dispatch.get(method).invoke(args);
			}
			catch (RuntimeException throwable) {
				throw throwable;
			}
			catch (Throwable throwable) {
				throw new RuntimeException(throwable);
			}
		};
	}

	/**
	 * If the method param of InvocationHandler.invoke is not accessible, i.e in a
	 * package-private interface, the fallback call will cause of access restrictions. But
	 * methods in dispatch are copied methods. So setting access to dispatch method
	 * doesn't take effect to the method in InvocationHandler.invoke. Use map to store a
	 * copy of method to invoke the fallback to bypass this and reducing the count of
	 * reflection calls.
	 * @return cached methods map for fallback invoking
	 */
	static Map<Method, Method> toFallbackMethod(Map<Method, InvocationHandlerFactory.MethodHandler> dispatch) {
		Map<Method, Method> result = new LinkedHashMap<Method, Method>();
		for (Method method : dispatch.keySet()) {
			method.setAccessible(true);
			result.put(method, method);
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FeignCircuitBreakerInvocationHandler) {
			FeignCircuitBreakerInvocationHandler other = (FeignCircuitBreakerInvocationHandler) obj;
			return this.target.equals(other.target);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.target.hashCode();
	}

	@Override
	public String toString() {
		return this.target.toString();
	}

}
```

具体我们关注<code>FeignCircuitBreakerInvocationHandler</code>类中的invoke方法，由此我们得知:

```java
String circuitName = circuitBreakerNameResolver.resolveCircuitBreakerName(feignClientName, target, method);
		CircuitBreaker circuitBreaker = circuitBreakerGroupEnabled ? factory.create(circuitName, feignClientName)
				: factory.create(circuitName);
		Supplier<Object> supplier = asSupplier(method, args);
		if (this.nullableFallbackFactory != null) {
			Function<Throwable, Object> fallbackFunction = throwable -> {
				Object fallback = this.nullableFallbackFactory.create(throwable);
				try {
					return this.fallbackMethodMap.get(method).invoke(fallback, args);
				}
				catch (Exception exception) {
					unwrapAndRethrow(exception);
				}
				return null;
			};
			return circuitBreaker.run(supplier, fallbackFunction);
		}
		return circuitBreaker.run(supplier);
```

- 获取断路器名称，查询对应的断路器<code>CircuitBreaker</code>
- <code>nullableFallbackFactory</code>不为空时,创建对应降级工厂，执行降级方法
- <code>circuitBreaker.run()</code>,执行断路器逻辑监听返回结果

## 5.3 FeignCircuitBreakerTargeter

<code>FeignCircuitBreakerTargeter</code>实现了Targeter接口，熟悉feign源码的同学肯定知道，实例化<code>feignClient</code>的时候，会调用<code>target</code>方法，实际会触发<code>
FeignBuiler</code>组件的<code>newInstance</code>。最终会调用Feign原生的<code>InvocationHandlerFactory.Default</code>工厂来创建<code>
FeignInvocationHandler</code>，由此我们实现<code>Targeter</code>，看一下<code>FeignCircuitBreakerTargeter.target</code>的实现逻辑

```java
@Override
public <T> T target(FeignClientFactoryBean factory, Feign.Builder feign, FeignContext context,
    Target.HardCodedTarget<T> target) {
  if (!(feign instanceof FeignCircuitBreaker.Builder)) {
    return feign.target(target);
  }
  FeignCircuitBreaker.Builder builder = (FeignCircuitBreaker.Builder) feign;
  String name = !StringUtils.hasText(factory.getContextId()) ? factory.getName() : factory.getContextId();
  Class<?> fallback = factory.getFallback();
  if (fallback != void.class) {
    return targetWithFallback(name, context, target, builder, fallback);
  }
  Class<?> fallbackFactory = factory.getFallbackFactory();
  if (fallbackFactory != void.class) {
    return targetWithFallbackFactory(name, context, target, builder, fallbackFactory);
  }
  return builder(name, builder).target(target);
}
 ``` 

```java
 private <T> T targetWithFallback(String feignClientName, FeignContext context, Target.HardCodedTarget<T> target,
			FeignCircuitBreaker.Builder builder, Class<?> fallback) {
		T fallbackInstance = getFromContext("fallback", feignClientName, context, fallback, target.type());
		return builder(feignClientName, builder).target(target, fallbackInstance);
	} 
 ``` 

由此可以看出：

- 判断<code>feign</code>是否是<code>FeignCircuitBreaker.Builder</code>，如果是的话，调用<code>feign.target(target)</code>方法
- 如果不是的话，获取<code>feignClientName</code>，判断返回结果是否可降级，如果返回结构是void，则不需要降级，
- 获取降级工厂，如果为空，则不进行降级，如果不为空，获取降级工厂实例，调用至<code>FeignCircuitBreakerTargeter.target</code>.

## 5.4 使用示例

- 配置文件

```yaml
feign:
  circuitbreaker:
    enabled: true
```

# 6 彩蛋

至此，我们已经完成了Circuitbreaker断路器源码解析。总的来说，因为Spring Cloud Circuitbreake的源码还算是比较简单的，所以理解起来还是蛮轻松的。

后续，可以自己扩展实现一下

# 公众号

学习不走弯路，关注公众号「凛冬王昭君」

![wechat-sparkzxl.jpg](https://oss.sparksys.top/sparkzxl-framework/wechat-sparkzxl.jpg)